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
import com.sun.jdi.Locatable;
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
        private Map<String, List<Tuple<Integer, Long>>> exploredPaths;
        private StepRequest stepRequest;
        private VirtualMachine vm;

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
                        System.out.println(event);
                        if (event instanceof BreakpointEvent)
                        {
                            if (stepRequest != null)
                            {
                                stepRequest.disable();
                            }

                            addExploredPath((Locatable) event);
                            //addStepRequest((BreakpointEvent) event);
                        }

                        if (event instanceof StepEvent)
                        {
                            addExploredPath((Locatable) event);
                        }

                        vm.resume();
                    }
                }
            } catch (InterruptedException e) {
                System.out.println("EventManager interrupted");
            }
        }

        public void reset()
        {
            exploredPaths = new HashMap<String, List<Tuple<Integer, Long>>>();
        }

        private void addExploredPath(Locatable event)
        {
            String methodName = event.location().method().name();
            int lineNumber = event.location().lineNumber();
            long codeIndex = event.location().codeIndex();

            if (!exploredPaths.containsKey(methodName))
            {
                exploredPaths.put(methodName, new ArrayList<Tuple<Integer, Long>>());
            }

            exploredPaths.get(methodName).add(new Tuple<Integer, Long>(lineNumber, codeIndex));
        }

        private void addStepRequest(BreakpointEvent event)
        {
            ThreadReference thread = event.thread();
            stepRequest = vm.eventRequestManager().createStepRequest(thread, StepRequest.STEP_MIN, StepRequest.STEP_OVER);
            stepRequest.setSuspendPolicy(EventRequest.SUSPEND_EVENT_THREAD);
            stepRequest.enable();
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

    public Map<String, List<Tuple<Integer, Long>>> getExploredPaths()
    {
        return eventManager.exploredPaths;
    }

    public void run() throws AbsentInformationException
    {
        setupInstrumentation();
        eventManager.start();
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
