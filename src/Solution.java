import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @ClassName: Solution
 * @author: csh
 * @Description:
 */
public class Solution  implements Comparable<Solution>{

    private static Problems problems;
    private static Random rand = new Random();
    private static double bCostFeasible = Double.MAX_VALUE;
    private static double bCostAll = Double.MAX_VALUE;
    private static int feas =0;
    private static int infeas=0;
    private int []p;  // department sequence
    private double cost =Double.MAX_VALUE;
    private int numInfeasible = 0;
    private boolean inHC = false;  //?

    private double[][] leftTops;
    private  static  double[][] centerPos; //center position of facility

    //protected double[][] dists; //distance between facilities
    private  static List<Double> dists;
    private int lastImprove = 0;
    private  int []bay ;      //?



    public int[] getPermutation() { return p; }
    public int getFacNum() { return p.length; }
    public double getCost() { return cost; }
    public void setLastImprove(int n) { this.lastImprove = n; }
    public int getLastImprove() { return this.lastImprove;}
    public boolean isFeasible() { return numInfeasible == 0; }
    public int[] getBay() { return bay; }

    public  Solution(){
        construct();
    }

    private void construct() {

        while (true){
            int[] fp = Tools.randomPermutation(0, problems.getDepartmentNum());
            p = new int[problems.getDepartmentNum()];
            bay = new int[problems.getDepartmentNum()];
            int index = 0;
            double width = problems.getWidth();
            double height = problems.getHeight();
            boolean feasible = true;

            while (index < fp.length && feasible) {
                double area = 0;
                List<Integer> list = new ArrayList<>();
                for (int i = index; i < fp.length; i++) {
                    list.add(fp[i]);
                    area += problems.getAreas(fp[i]);
                }
                int numInfea = 0;
                do {
                    numInfea = 0;
                    double w = area / height;
                    for (int fac : list) {
                        double h = problems.getAreas(fac)/w;
                        if (!problems.isValid(fac, w, h)) {
                            numInfea++;
                        }
                    }
                    if (numInfea > 0) {
                        area -= problems.getAreas(list.remove(list.size()-1));
                    }
                } while (numInfea > 0 && list.size() > 0);

                if (numInfea == 0 && list.size() > 0) {
                    for (int fac : list) {
                        p[index] = fac;
//    					System.out.print(fac+"\t");
                        if (index < bay.length) {
                            bay[index] = 0;
                        }
                        index++;
                    }
//    				if (index <= bay.length) {
                    bay[index-1] = 1;
//    				}
                } else {
                    feasible = false;
                }
            }

            if (feasible) {
                eval();
                return;
            }
        }

    }

    public Solution(int[] p, int[] bay) {
        this.p = p.clone();
        this.bay = bay.clone();
        eval();
    }

    public Solution(boolean initial) {
        if(initial) {
            problems =Problems.getProblem();
            int departmentNum = problems.getDepartmentNum();
            p = new int[departmentNum];
            cost=Double.MAX_VALUE;
            numInfeasible = 0;
            leftTops = new double[departmentNum][2];
            centerPos = new double[departmentNum][2];
            //dists = new double[departmentNum][departmentNum];
            dists = new ArrayList<>();
        }
    }
    public static void init() {
        problems = Problems.getProblem();
        bCostFeasible = Double.MAX_VALUE;
        bCostAll = Double.MAX_VALUE;
        feas = 0;
        infeas = 0;
        rand = new Random();
        centerPos = new double[problems.getDepartmentNum()][2];
    }

    //
    public static Solution make(int[] p, int[] bay) {
        return null;
    }

    //?
    protected void eval() {
        numInfeasible = 0;
        calcuCenters();   //
        if (!inHC || numInfeasible == 0) {
            calcuDist();
            calcuCost();
        }
        if (numInfeasible == 0) {
            feas++;
        } else {
            infeas++;
        }
    }

    // ?
    private void calcuCenters() {
//        if (Simulations.useRepair) {
//            calcuCentersWithRepair();
//        } else {
//            calcuCentersWithoutRepair();
//        }

    }

    private void calcuCentersWithoutRepair() {
        double x = 0, y = 0;
        int idx = 0;
        numInfeasible = 0;
        while (idx < p.length) {
            List<Integer> list = new ArrayList<>();
            while (idx < bay.length && bay[idx] == 0) {
                list.add(idx);
                idx++;
            }
            list.add(idx);
            idx++;

            //area of facility in list
            double area = 0;
            for (int i : list) {
                area += problems.getAreas(p[i]);     // 第一个隔间 总面积
            }
        }

    }

    private void calcuCentersWithRepair() {

    }


    //曼哈顿距离（rectilinear = |xi − xj| + |yi − yj|）/欧氏距离（euclidean = [(xj − xi)2 + (yj − yi)2]1/2）
    private void calcuDist() {
        dists = new ArrayList<>(); //
        for (int[] edge : problems.getEdges()) {
            int fac1 = edge[0];
            int fac2 = edge[1];
            if (problems.isRect()) {
                dists.add(Math.abs(centerPos[fac1][0] - centerPos[fac2][0]) +
                        Math.abs(centerPos[fac1][1] - centerPos[fac2][1]));
            } else {
                dists.add(Math.sqrt( Math.pow(centerPos[fac1][0] - centerPos[fac2][0],2) +
                        Math.pow(centerPos[fac1][1] - centerPos[fac2][1],2)));
            }
        }
    }


   // min Z
    private void calcuCost() {
        Problems problem = Problems.getProblem();
        cost =0;
        //fij * dij
        for (int i = 0; i < problem.getEdges().size(); i++) {
            cost += dists.get(i) * problem.getFlow(i);
        }

        //Penalty for infeasible solution   k * M        ????
//        cost += Math.pow(numInfeasible, Simulations.beta) * (Solution.bCostFeasible);// - Solution.bCostAll + 1000);
//        if (numInfeasible == 0 && cost < Solution.bCostFeasible) {
//            Solution.bCostFeasible = cost;
//        }
//        if (cost < Solution.bCostAll) {
//            Solution.bCostAll = cost;
//        }

    }


    //领域解
    public Solution localSearch() {
        Solution bs = this;
        boolean improved = true;
        int[] p = this.p.clone();
        int[] bay = this.bay.clone();
        while (improved) {
            improved = false;
            for (int i = 0; i < p.length - 1; i++) {
                for (int j = i + 1; j < p.length; j++) {
                    int from = p[i];
                    int to = p[j];
                    p[i] = to;
                    p[j] = from;
                    Solution ns = new Solution(p, bay);
                    if (ns.compareTo(bs) > 0) {
                        improved = true;
                        bs = ns;
                    } else {
                        p[i] = from;
                        p[j] = to;
                    }
                }
            }

            //to be implemented to create new p or top

            //End of creating new p or top
        }
        return bs;
    }


    public String toString() {
        String str = (numInfeasible == 0) + "\t" + cost + "\n"; //cost
        for (int i = 0; i < p.length; i++) {
            str += centerPos[i][0] + "\t" + centerPos[i][1] + "\n";
        }

        for (int i = 0; i < p.length; i++) {
            str += p[i] + "\t";
        }
        str += "\n";

        for (int i = 0; i < bay.length; i++) {
            str += bay[i] + "\t";
        }
        str += "\n";
        return str;
    }



    public int 	compareTo(Solution s) {
        if (this.isFeasible() && !s.isFeasible()) {
            return 1;
        } else if (!this.isFeasible() && s.isFeasible()) {
            return -1;
        } else if (this.cost < s.cost) {
            return 1;
        } else if (this.cost == s.cost) {//
            return 0;
        } else {
            return -1;
        }
    }



    public static void main(String[] args) throws IOException {
        String fileName = "datas/Kang13-old/10AB20-ar04.txt";
        Problems.setFileName(fileName);
        //
        Solution solution = new Solution(true);
        solution.construct();
        System.out.println(solution.toString());
        //
        int[] p = new int[]{11, 16, 13, 17, 12, 15, 10, 14, 3, 19, 4, 2, 20,8,7,9,5,1, 6, 18};//{5,8,6,3,2,1,4,7};//11, 16, 13, 17, 12, 15, 10, 14, 3, 19, 4, 2, 20-8-7-9-5-1, 6, 18
        p = new int[]{18, 20 , 6, 4, 2, 7, 8, 5 , 1, 19, 3, 10 , 12, 9, 14 , 17, 13, 15 ,16, 11};//{9, 3, 10, 7, 12, 6, 11, 5, 4, 8, 2, 1};//{20, 30, 19, 26 | 29, 27 | 25, 3, 1 | 4, 24, 17 | 18, 28, 23 | 15, 11, 9, 8 | 12, 7 | 16, 14, 13, 10, 5, 6, 2, 21, 22};
        for (int i = 0; i < p.length; i++) {
            p[i]--;
        }
        int[] bay = new int[]{1,1,1,1,1,1,1,1,1,1,1,1,0,0,0,0,0,1,1};//{0,0,0,1,0,0,0};//
        bay = new int[]{0,1,0,0,0,0,0,1,0,0,0,1,0,0,1,0,0,1,0,1};//{0,1,0,0,1,0,1,0,1,0,1,1};
        Solution.init();
        Solution solution1 = new Solution(p, bay);
        System.out.println(solution1);
        Solution local = solution1.localSearch();   // cost unimp
        System.out.println(local);
    }


}
