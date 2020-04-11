import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Random;

/**
 * @ClassName: Tools
 * @author: csh
 * @date: 2020/4/9  14:13
 * @Description:
 */
public class Tools {

    public  static Random rand =new Random();

    public static int[] randomPermutation(int i, int departmentNum) {
        int []p = new int[departmentNum];
        ArrayList<Integer> integers = new ArrayList<>();
        for (int i1 = 0; i1 < departmentNum; i1++) {
            integers.add(i1);
        }
        Collections.shuffle(integers);
//        integers.stream().forEach(s-> System.out.println(s));
        p = integers.stream().mapToInt(Integer::valueOf).toArray();
        return p;
    }




    public static void main(String[] args) {
        int []p = new int[4];
        ArrayList<Integer> integers = new ArrayList<>();
        integers.add(1);
        integers.add(2);
        integers.add(3);
        integers.add(41);
        Integer remove = integers.remove(3);
        System.out.println(remove);


    }
}
