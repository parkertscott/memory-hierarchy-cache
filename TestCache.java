public class TestCache{
    public static void main(String[] args)
    {
        //double[] accessTimes = new double[]{0.14682, 0.154496, 0.185685, 0.211173, 0.233936, 0.27125, 0.319481, 0.38028, 0.457685};
        //double[] accessTimesL2 = new double[]{0.180686, 0.189065, 0.212911, 0.254354, 0.288511, 0.341213};
        double[] accessTimesL11 = new double[]{ 0.114797, 0.12909, 0.147005, 0.16383, 0.198417, 0.233353, 0.294627, 0.3668, 0.443812, 0.563451, 0.69938};
        double[] accessTimesL12 = new double[]{ 0.140329, 0.161691, 0.181131, 0.194195, 0.223917, 0.262446, 0.300727, 0.374603, 0.445929, 0.567744, 0.706046};
        double[] accessTimesL14 = new double[]{ 0.14682, 0.154496, 0.185685, 0.211173, 0.233936, 0.27125, 0.319481, 0.38028, 0.457685, 0.564418, 0.699607};
        double[] accessTimesL18 = new double[]{ 0.14682, 0.180686, 0.189065, 0.212911, 0.254354, 0.288511, 0.341213, 0.401236, 0.458925, 0.578177, 0.705819};
        double[] accessTimesL1FA = new double[]{ 0.155484, 0.180686, 0.182948, 0.198581, 0.205608, 0.22474, 0.276281, 0.322486, 0.396009, 0.475728, 0.588474};
        int counter = 0;
        // Graph 3
        for(int x = 1; x <= 1024; x*=2)
        {
            double[] metrics = sim_cache.testSimulator(new String[]{"32", "" + (x * 1024), "" + x, "0", "0", "0", "0", "D:/Escuela/2020 Fall/CDA5106/MP1/EclipseProject/MP1/src/mp1/trace_files/gcc_trace.txt"});
            System.out.println("Test - " + x + ":");
            System.out.println("L1 Miss Rate: " + metrics[4]);
            System.out.println("AAT: " + CalculateAverageAccessTimeL1(accessTimesL1FA[counter], (int)metrics[0], (int)metrics[1], (int)metrics[2], (int)metrics[3], 100));
            counter++;
        }
    }
    private static double CalculateAverageAccessTimeL1(double htl1, int l1rm, int  l1wm, int l1r,  int l1w,  int miss)
    {
        return (double)htl1 + (((double)l1rm + (double)l1wm)/ ((double)l1r + (double)l1w)) * (double)miss;
    }
    
    private static double CalculateAverageAccessTimeL1andL2(double htl1, int l1rm, int  l1wm, int l1r,  int l1w, double htl2, int l2rm, int miss)
    {
        return (double)htl1 + (((double)l1rm + (double)l1wm)/ ((double)l1r + (double)l1w)) * (double)htl2 + ((double)l2rm/(l1r + l1w)) * miss;
    }
}