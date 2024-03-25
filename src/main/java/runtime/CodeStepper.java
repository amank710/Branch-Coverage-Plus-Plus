package runtime;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Method;
import java.util.Map;

import com.sun.jdi.AbsentInformationException;
import com.sun.jdi.Bootstrap;
import com.sun.jdi.ClassType;
import com.sun.jdi.connect.Connector;
import com.sun.jdi.connect.IllegalConnectorArgumentsException;
import com.sun.jdi.connect.LaunchingConnector;
import com.sun.jdi.connect.VMStartException;
import com.sun.jdi.event.ClassPrepareEvent;
import com.sun.jdi.event.BreakpointEvent;
import com.sun.jdi.event.Event;
import com.sun.jdi.event.EventSet;
import com.sun.jdi.event.LocatableEvent;
import com.sun.jdi.event.StepEvent;
import com.sun.jdi.IncompatibleThreadStateException;
import com.sun.jdi.LocalVariable;
import com.sun.jdi.Location;
import com.sun.jdi.request.BreakpointRequest;
import com.sun.jdi.request.ClassPrepareRequest;
import com.sun.jdi.request.StepRequest;
import com.sun.jdi.StackFrame;
import com.sun.jdi.Value;
import com.sun.jdi.VirtualMachine;

class CodeStepper
{
    class CodeStepperMain
    {
        static Method executable;

        public static void main(String[] args)
        {
            System.out.println("Executing code stepper...");
            //executable.invoke(null);
        }
    }

    public class CodeStepperTest
    {
        public static void main(String[] args)
        {
            System.out.println("Executing code stepper test...");
        }
    }

    CodeStepper(Method method)
    {
        RunnerSingleton.executable = method;
    }

    private void enableClassPrepareRequest(VirtualMachine vm)
    {
        ClassPrepareRequest classPrepareRequest = vm.eventRequestManager().createClassPrepareRequest();
        classPrepareRequest.addClassFilter(RunnerSingleton.class.getName());
        classPrepareRequest.enable();
    }

    public void displayVariables(LocatableEvent event) throws IncompatibleThreadStateException, AbsentInformationException {
        StackFrame stackFrame = event.thread().frame(0);
        if(stackFrame.location().toString().contains(RunnerSingleton.class.getName())) {
            Map<LocalVariable, Value> visibleVariables = stackFrame.getValues(stackFrame.visibleVariables());
            System.out.println("Variables at " +stackFrame.location().toString() +  " > ");
            for (Map.Entry<LocalVariable, Value> entry : visibleVariables.entrySet()) {
                System.out.println(entry.getKey().name() + " = " + entry.getValue());
            }
        }
    }

    public void run() throws IOException, IllegalConnectorArgumentsException, VMStartException, InterruptedException, AbsentInformationException, IncompatibleThreadStateException
    {
        VirtualMachine vm = null;

        try {
            vm = connectToMachine();
            enableClassPrepareRequest(vm);

            vm.allClasses().forEach(clazz -> {
                System.out.println(clazz.name());
            });

            EventSet eventSet = null;
            while ((eventSet = vm.eventQueue().remove()) != null)
            {
                for (Event event : eventSet)
                {
                    if (event instanceof ClassPrepareEvent) {
						ClassPrepareEvent evt = (ClassPrepareEvent) event;
						ClassType classType = (ClassType) evt.referenceType();

						Location location = classType.locationsOfLine(11).get(0);
						BreakpointRequest bpReq = vm.eventRequestManager().createBreakpointRequest(location);
						bpReq.enable();

					}

					/*
					 * If this is BreakpointEvent, then read & print variables.
					 */
					if (event instanceof BreakpointEvent) {
						// disable the breakpoint event
						event.request().disable();

                        StepRequest stepRequest = vm.eventRequestManager().createStepRequest(((BreakpointEvent) event).thread(), StepRequest.STEP_LINE, StepRequest.STEP_OVER);
                        stepRequest.enable();    

                        System.out.println(((BreakpointEvent) event).location().lineNumber());
					}

                    if (event instanceof StepEvent) {
                        event.request().disable();

                        System.out.println(((StepEvent) event).location().toString());
                        displayVariables((LocatableEvent) event);

                        StepRequest stepRequest = vm.eventRequestManager().createStepRequest(((StepEvent) event).thread(), StepRequest.STEP_LINE, StepRequest.STEP_OVER);
                        stepRequest.enable();    

                    }

                    if (event instanceof ClassPrepareEvent)
                    {
                        System.out.println("ClassPrepareEvent");
                    }
                    vm.resume();
                }
            }
        } catch (Exception e) {
            throw e;
        } finally {
            System.out.println("Closing virtual machine...");
            InputStreamReader reader = new InputStreamReader(vm.process().getInputStream());
            InputStreamReader errorReader = new InputStreamReader(vm.process().getErrorStream());

            OutputStreamWriter writer = new OutputStreamWriter(System.out);
            OutputStreamWriter errorWriter = new OutputStreamWriter(System.err);

            char[] buffer = new char[512];
            char[] errorBuffer = new char[512];
            reader.read(buffer);
            errorReader.read(errorBuffer);

            writer.write(buffer);
            errorWriter.write(errorBuffer);

            writer.flush();
            errorWriter.flush();
        }
    }

    private VirtualMachine connectToMachine() throws IOException, IllegalConnectorArgumentsException, VMStartException
    {
        LaunchingConnector connector = Bootstrap.virtualMachineManager().defaultConnector();
        Map<String, Connector.Argument> arguments = connector.defaultArguments();
        arguments.get("main").setValue(RunnerSingleton.class.getName());
        arguments.get("options").setValue("-cp target/classes");

        VirtualMachine vm = connector.launch(arguments);

        return vm;
    }
}
