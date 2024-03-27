package runtime;

import common.util.Tuple;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.*;

import com.sun.jdi.AbsentInformationException;
import com.sun.jdi.Bootstrap;
import com.sun.jdi.ClassType;
import com.sun.jdi.connect.AttachingConnector;
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
import com.sun.jdi.Method;
import com.sun.jdi.request.BreakpointRequest;
import com.sun.jdi.request.ClassPrepareRequest;
import com.sun.jdi.request.EventRequest;
import com.sun.jdi.request.StepRequest;
import com.sun.jdi.StackFrame;
import com.sun.jdi.ThreadReference;
import com.sun.jdi.Value;
import com.sun.jdi.VirtualMachine;

class CodeStepper
{
    private Map<String, Set<String>> instrumentedMethods;
    private EventManager eventManager;
    private VirtualMachine vm;

    class EventManager extends Thread
    {
        VirtualMachine vm;
        private Map<String, List<Tuple<Integer, Long>>> exploredPaths;

        public EventManager(VirtualMachine vm)
        {
            this.vm = vm;
        }

        @Override
        public void run()
        {
            EventSet eventSet = null;

            try
            {
                while ((eventSet = vm.eventQueue().remove()) != null)
                {
                    for (Event event : eventSet)
                    {
                        System.out.println("Event: " + event.toString());
                        if (event instanceof ClassPrepareEvent)
                        {
                            System.out.println("ClassPrepareEvent");
                        }
                        vm.resume();

                        if (event instanceof BreakpointEvent)
                        {
                            System.out.println(((BreakpointEvent) event).location().lineNumber());
                        }
                    }
                }
            } catch (InterruptedException e) {
                System.out.println("EventManager interrupted");
            }
            System.out.println("EventManager exiting...");
        }

        public void reset()
        {
            exploredPaths = new HashMap<String, List<Tuple<Integer, Long>>>();
        }
    }


    CodeStepper(Map<String, Set<String>> instrumentedMethods)
    {
        this.instrumentedMethods = instrumentedMethods;

        try
        {
            connectToCurrentMachine();
        }
        catch (Exception ex)
        {
            System.out.println("[CodeStepper] Could not connect to current machine: " + ex.getMessage());
            System.exit(1);
        }

        eventManager = new EventManager(vm); 
    }

    private void enableClassPrepareRequest(VirtualMachine vm)
    {
        ClassPrepareRequest classPrepareRequest = vm.eventRequestManager().createClassPrepareRequest();
        classPrepareRequest.addClassFilter(InstrumentedTestExtension.class.getName());
        classPrepareRequest.setSuspendPolicy(EventRequest.SUSPEND_EVENT_THREAD);
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
        try {
            setupInstrumentation();
            eventManager.start();
        } finally {
            //System.out.println("Closing virtual machine...");
            //InputStreamReader reader = new InputStreamReader(vm.process().getInputStream());
            //InputStreamReader errorReader = new InputStreamReader(vm.process().getErrorStream());

            //OutputStreamWriter writer = new OutputStreamWriter(System.out);
            //OutputStreamWriter errorWriter = new OutputStreamWriter(System.err);

            //char[] buffer = new char[512];
            //char[] errorBuffer = new char[512];
            //reader.read(buffer);
            //errorReader.read(errorBuffer);

            //writer.write(buffer);
            //errorWriter.write(errorBuffer);

            //writer.flush();
            //errorWriter.flush();
        }
    }

    public void reset()
    {
        eventManager.reset();
    }

    private void connectToCurrentMachine() throws IllegalConnectorArgumentsException, IOException
    {
        AttachingConnector ac = Bootstrap.virtualMachineManager().attachingConnectors().stream().filter(c -> c.name().equals("com.sun.jdi.ProcessAttach")).findFirst()
            .orElseThrow(() -> new RuntimeException("No process attaching connector found"));
        Map<String, Connector.Argument> arguments = ac.defaultArguments();
        arguments.get("pid").setValue(String.valueOf(ProcessHandle.current().pid()));

        System.out.println("Attaching to current process...");
        vm = ac.attach(arguments);
    }

    private void addBreakpoint(Location location)
    {
        BreakpointRequest bpReq = vm.eventRequestManager().createBreakpointRequest(location);
        bpReq.setSuspendPolicy(EventRequest.SUSPEND_EVENT_THREAD);
        bpReq.enable();
    }

    private void setupInstrumentation() throws AbsentInformationException
    {
        for (Map.Entry<String, Set<String>> entry : instrumentedMethods.entrySet())
        {
            String className = entry.getKey();
            Set<String> methodNames = entry.getValue();

            try
            {
                ClassType classType = (ClassType) vm.classesByName(className).get(0);
                for (String methodName : methodNames)
                {
                    Method method = classType.methodsByName(methodName).get(0);

                    for (Location location : method.allLineLocations())
                    {
                        addBreakpoint(location);
                    }
                }
            } catch (AbsentInformationException e) {
                System.out.println("[CodeStepper] Please compile the class with debug information");
                throw e;
            }
        }
    }
}
