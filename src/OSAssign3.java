import java.util.*;

class Process
{
    int processID;
    int burstTime;
    int remainingBurstTime;
    int arrivalTime;
    int priority;
    int quantum;
    int factor;
    int finishTime;
    int waitingTime;
    int turnaroundTime;

    public Process(int processID, int burstTime, int arrivalTime, int priority, int quantum) {
        this.processID = processID;
        this.burstTime = burstTime;
        this.arrivalTime = arrivalTime;
        this.priority = priority;
        this.quantum = quantum;
        this.remainingBurstTime = burstTime;
    }


}

public class OSAssign3 {

	private static double calculateV1(ArrayList<Process> p) {
        double maxArrival = -1;

        for (Process p1 : p) {
            if (p1.arrivalTime > maxArrival) {
                maxArrival = p1.arrivalTime;
            }
        }
        return maxArrival > 10 ? (double)(maxArrival / 10.0) : 1;
    }
	
    public static double calculateV2(ArrayList<Process> allProcess) {

        double tempMaxRemaining = Integer.MIN_VALUE;
        for (Process process : allProcess) {
            if (tempMaxRemaining < process.remainingBurstTime)
                tempMaxRemaining = process.remainingBurstTime;
        }
        return tempMaxRemaining > 10 ? (tempMaxRemaining / 10) : 1;
    }


    public static void factor(ArrayList<Process> allProcess, double v1, double v2) {

        for (Process process : allProcess) {
            process.factor = (int) ((10-process.priority) + Math.ceil(process.arrivalTime/v1) + Math.ceil(process.remainingBurstTime/v2));
        }
    }

    public static int returnMinFactor(Queue<Process> q) {

        int minFactor = Integer.MAX_VALUE;
        for (Process process : q) {
            if (process.factor < minFactor) {
                minFactor = process.factor;
            }
        }
        int index = 0;
        for (Process process : q) {
            if (process.factor == minFactor)
                return index;
            index++;
        }
        return -1;
    }

    
    
    public static <T> T get(Queue<T> queue, int index) {
        synchronized (queue) {
            int size = queue.size();
            if (index < 0 || size < index + 1) {
                return null;
            }

            T element = null;
            for (int i = 0; i < size; i++) {
                if (i == index) {
                    element = queue.remove();
                } else {
                    queue.add(queue.remove());
                }
            }

            return element;     
        }
    }
    
    public static void AGAT(ArrayList<Process> allProcess) {

    	Queue<Process> deadQueue = new LinkedList<>();
        
    	Queue<Process> readyQueue = new LinkedList<>();
    	readyQueue.add(allProcess.get(0));
    	Process tempProcess = readyQueue.remove();
    	
        boolean arrived[] = new boolean[allProcess.size()];
        for(int i=0;i<allProcess.size();i++) {
        	arrived[i] = false;
        }
        

        int totalTime = 0;
        
        
        while (allProcess.size()!=deadQueue.size()) {     

            double v2 = calculateV2(allProcess);

            System.out.println("P"+tempProcess.processID);
            
            String printQuantum = "(";
            for (int i = 0; i <= allProcess.size() - 1; i++) {

                if (i != allProcess.size() - 1)
                    printQuantum += allProcess.get(i).quantum + ", ";
                else
                    printQuantum += allProcess.get(i).quantum + ")";

            }
            
            System.out.println(printQuantum);
            
            String printFactor = "(";
            for (int i = 0; i <= allProcess.size() - 1; i++) {
            	
                if (i != allProcess.size() - 1)
                {
                	if(deadQueue.contains(allProcess.get(i)))
                		printFactor += "-" + ", ";
                	else printFactor += allProcess.get(i).factor + ", ";
                }
                	
                else
                {
                	if(deadQueue.contains(allProcess.get(i)))
                		printFactor += "-" + ") ";
                	else printFactor += allProcess.get(i).factor + ")";
                }
                	

            }
            
            System.out.println(printFactor);
            int roundQuantum = (int) Math.ceil(0.4 * tempProcess.quantum);
            int quantumRemaining = tempProcess.quantum - roundQuantum;
            
        	if(tempProcess.remainingBurstTime<roundQuantum )
        		roundQuantum = tempProcess.remainingBurstTime;
            totalTime += roundQuantum;
            tempProcess.remainingBurstTime -= roundQuantum;
            
            

            while (true) {
            	
                for (int i=1;i<allProcess.size();i++) 
                {
                    if (totalTime >= allProcess.get(i).arrivalTime && !arrived[i]) {
                        readyQueue.add(allProcess.get(i));
                        arrived[i] = true;
                    }
                }
                
                if (readyQueue.isEmpty()) 
                {
                    totalTime++;
                    tempProcess.remainingBurstTime--;
                    quantumRemaining--;
                    
                    if(tempProcess.remainingBurstTime==0)
                    {
                    	tempProcess.quantum = 0;
                    	deadQueue.add(tempProcess);
                    	tempProcess.finishTime = totalTime;
                    	break;
                    }
                    	
                }
                
                else
                {
                    if (quantumRemaining > 0) 
                    {
                    	readyQueue.add(tempProcess);
                    	
                    	factor(allProcess, calculateV1(allProcess), v2);
                    	Process chosenProcess = get(readyQueue, returnMinFactor(readyQueue));
                        
                        if(tempProcess.equals(chosenProcess))
                        {                            
                        	
                        	if(tempProcess.remainingBurstTime<quantumRemaining )
                            	quantumRemaining = tempProcess.remainingBurstTime;
                            
                        	totalTime += quantumRemaining;
                            
                            tempProcess.remainingBurstTime -= quantumRemaining;
                            
                            
                            if (tempProcess.remainingBurstTime > 0) 
                            {
                            	tempProcess.quantum += 2;
                            	readyQueue.add(chosenProcess);
                            }
                            else
                            {
                            	tempProcess.quantum = 0;
                            	tempProcess.finishTime = totalTime;
                            	if(tempProcess.processID==1)
                            		tempProcess.finishTime--;
                            	deadQueue.add(tempProcess);
                            }
                            
                            tempProcess = readyQueue.remove();
                        }
                        else
                        {
                            tempProcess.quantum += quantumRemaining;
                                                                              
                            tempProcess = chosenProcess;
                        }
                        
                    }
                    else
                    {
                        if (tempProcess.remainingBurstTime > 0) 
                        {
                            tempProcess.quantum += 2;
                            readyQueue.add(tempProcess);
                        }
                        else
                        {
                        	tempProcess.quantum = 0;
                        	tempProcess.finishTime = totalTime;
                        	deadQueue.add(tempProcess);
                        }
                            
                        tempProcess = readyQueue.remove();
                    }
                    break;
                }                
            }
            
            
        }
        double sumWaiting = 0;
        double sumTurn = 0;
        for(Process p:allProcess)
        {
        	p.turnaroundTime = p.finishTime-p.arrivalTime;
        	p.waitingTime = p.turnaroundTime-p.burstTime;
        	sumWaiting+= p.waitingTime;
        	sumTurn+= p.turnaroundTime;
        }
        double avgWaiting = sumWaiting/allProcess.size();
        double avgTurn = sumTurn/allProcess.size();
        System.out.print("\nProcesses   Arrival time   Waiting time   burst time   Turn around time   Finish time\n");
        for (int i = 0; i < allProcess.size(); i++) {
            System.out.print(" " + "P" + allProcess.get(i).processID + "\t\t" + allProcess.get(i).arrivalTime + "\t\t " + allProcess.get(i).waitingTime + "\t\t "+ allProcess.get(i).burstTime +"\t\t "+ allProcess.get(i).turnaroundTime +"\t\t "+ allProcess.get(i).finishTime + "\n");
        }
        System.out.print("\nAverage waiting time = " + avgWaiting);
        System.out.print("\nAverage turn around time = " + avgTurn);
    }

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        
        //System.out.println("Enter number of processes: ");
        //int n = sc.nextInt();

        ArrayList<Process> processes = new ArrayList<>();
        /*
        for (int i = 0; i < n; i++) {

            System.out.print("Process #" + (i + 1) + " burst time: ");
            int burstTime = sc.nextInt();

            System.out.print("Process #" + (i + 1) + " arrival time: ");
            int arrivalTime = sc.nextInt();

            System.out.print("Process #" + (i + 1) + " priority: ");
            int priority = sc.nextInt();

            System.out.print("Process #" + (i + 1) + " quantum: ");
            int quantum = sc.nextInt();

            System.out.println();

            processes.add(new Process((i + 1), burstTime, arrivalTime, priority, quantum));
        }
        */
        processes.add(new Process(1, 17, 0, 4, 4));
        processes.add(new Process(2, 6, 3, 9, 3));
        processes.add(new Process(3, 10, 4, 3, 5));
        processes.add(new Process(4, 4, 29, 8, 2));

        AGAT(processes);
		

        

    }
}