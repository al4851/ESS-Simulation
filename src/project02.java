import java.util.*;

public class project02 {
    public static void main(String[] args) {
        if (args.length == 0 || args.length > 4) {
            System.err.println("Usage: ./project02 popSize [percentHawks] [resourceAmt] [costHawk-Hawk]");
            System.exit(0);
        } else {
            run(args);
        }
    }

    public static void run(String[] args) {
        List<Individual> individuals = create(args);
        Scanner scan = new Scanner(System.in);
        int input = 0;
        int encounter = 0;
        while (input != 8) {
            menu();
            input = Integer.parseInt(scan.nextLine());
            System.out.println();
            while (input < 1 || input > 8) {
                System.out.println("Unexpected value");
                System.out.println("Please try again");
                System.out.print("> ");
                input = Integer.parseInt(scan.nextLine());
            }
            switch (input) {
                case 1 -> stats();
                case 2 -> displayInfo(individuals);
                case 3 -> sortedInfo(individuals);
                case 4 -> {
                    for (int i = 0; i < 1000; i++) {
                        if (Individual.living < 2) {
                            System.out.println("Living: " + Individual.living);
                            System.out.println("Unable to perform interactions");
                            break;
                        }
                        encounter++;
                        System.out.println("Encounter: " + encounter);
                        interactions(individuals);
                    }
                }
                case 5 -> {
                    for (int i = 0; i < 10000; i++) {
                        if (Individual.living < 2) {
                            System.out.println("Living: " + Individual.living);
                            System.out.println("Unable to perform interactions");
                            break;
                        }
                        encounter++;
                        System.out.println("Encounter: " + encounter);
                        interactions(individuals);
                    }
                }
                case 6 -> {
                    System.out.println("Enter the number of interaction");
                    System.out.print("> ");
                    int n = Integer.parseInt(scan.nextLine());
                    for (int i = 0; i < n; i++) {
                        if (Individual.living < 2) {
                            System.out.println("Living: " + Individual.living);
                            System.out.println("Unable to perform interactions");
                            break;
                        }
                        encounter++;
                        System.out.println("Encounter: " + encounter);
                        interactions(individuals);
                    }
                }
                case 7 -> {
                    String s = "";
                    while (!(s.equals("stop"))) {
                        if (Individual.living < 2) {
                            System.out.println("Living: " + Individual.living);
                            System.out.println("Unable to perform interactions");
                            break;
                        }
                        encounter++;
                        System.out.println("Encounter: " + encounter);
                        interactions(individuals);
                        s = scan.nextLine();
                    }
                }
                case 8 -> System.out.println("Quit");
            }
            System.out.println();
        }
    }

    public static List<Individual> create(String[] args) {
        Individual.population = Integer.parseInt(args[0]);
        Individual.hawkPercent = 20;
        Individual.resourceAmount = 50;
        Individual.hawkPenalty = 100;
        Individual.living = Individual.population;
        if (args.length == 2)
            Individual.hawkPercent = Integer.parseInt(args[1]);

        if (args.length == 3)
            Individual.resourceAmount = Integer.parseInt(args[2]);
        if (args.length == 4)
            Individual.hawkPenalty = Integer.parseInt(args[3]);

        List<Individual> individuals = new ArrayList<>();
        int numOfHawks = Individual.population * Individual.hawkPercent / 100;
        for (int i = 0; i < numOfHawks; i++) {
            individuals.add(new Individual(i + 1, "Hawk"));
        }
        for (int i = numOfHawks; i < Individual.population; i++) {
            individuals.add(new Individual(i + 1, "Dove"));
        }
        return individuals;
    }

    public static void stats() {
        System.out.printf("Population size: %d\n", Individual.population);
        System.out.println("Percentage of Hawks: " + Individual.hawkPercent + "%");
        int numOfHawks = Individual.population * Individual.hawkPercent / 100;
        System.out.printf("Number of Hawks: %d\n\n", numOfHawks);
        System.out.println("Percentage of Doves: " + (100 - Individual.hawkPercent) + "%");
        int numOfDoves = Individual.population - numOfHawks;
        System.out.printf("Number of Doves: %d\n\n", numOfDoves);
        System.out.println("Each resource is worth: " + Individual.resourceAmount);
        System.out.println("Cost of Hawk-Hawk interaction: " + Individual.hawkPenalty);
    }

    public static void displayInfo(List<Individual> individuals) {
        for (Individual i : individuals) {
            System.out.printf("Individual[%d]=%s:%d\n", i.id, i.status, i.resource);
        }
        System.out.println("Living: " + Individual.living);
    }

    public static void sortedInfo(List<Individual> individuals) {
        List<SortedIndividual> clone = new ArrayList<>(individuals.size());
        for (Individual i : individuals) {
            clone.add(new SortedIndividual(i.status, i.resource));
        }
        clone.sort(Collections.reverseOrder());
        for (SortedIndividual i : clone) {
            System.out.printf("%s:%d\n", i.status, i.resource);
        }
    }

    public static void interactions(List<Individual> individuals) {
        Random random = new Random();
        int first = random.nextInt(individuals.size());
        while (individuals.get(first).isDead)
            first = random.nextInt(individuals.size());

        int second = random.nextInt(individuals.size());
        while (individuals.get(second).isDead || first == second) {
            second = random.nextInt(individuals.size());
        }
        Individual one = individuals.get(first);
        Individual two = individuals.get(second);

        if (one.status.equals("Dove") && two.status.equals("Dove")) {
            doveDove(one, two);
        } else if (one.status.equals("Hawk") && two.status.equals("Dove")) {
            hawkDove(one, two);
        } else if (one.status.equals("Dove") && two.status.equals("Hawk")) {
            hawkDove(two, one);
        } else if (one.status.equals("Hawk") && two.status.equals("Hawk")) {
            hawkHawk(one, two);
        }
        System.out.println();
    }

    public static void doveDove(Individual one, Individual two) {
        System.out.printf("Individual %d: %s\n", one.id, one.status);
        System.out.printf("Individual %d: %s\n", two.id, two.status);
        one.resource += Individual.resourceAmount / 2;
        two.resource += Individual.resourceAmount / 2;
        System.out.printf("%s/%s: %s: +%d\t%s: +%d\n",
                one.status, two.status,
                one.status, Individual.resourceAmount / 2,
                two.status, Individual.resourceAmount / 2);
        System.out.printf("Individual %d=%d\t\t\tIndividual %d=%d\n",
                one.id, one.resource,
                two.id, two.resource);
    }

    public static void hawkDove(Individual one, Individual two) {
        System.out.printf("Individual %d: %s\n", one.id, one.status);
        System.out.printf("Individual %d: %s\n", two.id, two.status);
        one.resource += Individual.resourceAmount;
        System.out.printf("%s/%s: %s: +%d\t%s: +0\n",
                one.status, two.status,
                one.status, Individual.resourceAmount,
                two.status);
        System.out.printf("Individual %d=%d\t\t\tIndividual %d=%d\n",
                one.id, one.resource,
                two.id, two.resource);
    }

    public static void hawkHawk(Individual one, Individual two) {
        System.out.printf("Individual %d: %s\n", one.id, one.status);
        System.out.printf("Individual %d: %s\n", two.id, two.status);
        int resource = Individual.hawkPenalty - Individual.resourceAmount;
        one.resource -= resource;
        two.resource -= Individual.hawkPenalty;
        System.out.printf("%s/%s: %s: -%d\t%s: -%d\n",
                one.status, two.status,
                one.status, resource,
                two.status, Individual.hawkPenalty);
        if (one.resource < 0) {
            one.dead();
            System.out.println("Hawk one has died!");
        }
        if (two.resource < 0) {
            two.dead();
            System.out.println("Hawk two has died!");
        }
        System.out.printf("Individual %d=%d\t\t\tIndividual %d=%d\n",
                one.id, one.resource,
                two.id, two.resource);
    }

    public static void menu() {
        String s = "";
        s += "===============MENU=============\n";
        s += "1 ) Starting Stats\n";
        s += "2 ) Display Individuals and Points\n";
        s += "3 ) Display Sorted\n";
        s += "4 ) Have 1000 interactions\n";
        s += "5 ) Have 10000 interactions\n";
        s += "6 ) Have N interactions\n";
        s += "7 ) Step through interactions \"Stop\" to return to menu\n";
        s += "8 ) Quit\n";
        s += "================================\n";
        s += "> ";
        System.out.print(s);
    }

    static class Individual {
        static int population;
        static int hawkPercent;
        static int resourceAmount;
        static int hawkPenalty;
        static int living;

        int id;
        int resource;
        String status;
        boolean isDead;

        public Individual(int id, String status) {
            this.id = id;
            this.resource = 0;
            this.status = status;
            this.isDead = false;
        }

        public void dead() {
            this.status = "DEAD";
            this.isDead = true;
            living--;
        }
    }

    static class SortedIndividual implements Comparable<SortedIndividual>{
        String status;
        int resource;

        public SortedIndividual(String status, int resource) {
            this.status = status;
            this.resource = resource;
        }

        @Override
        public int compareTo(SortedIndividual o) {
            if (this.resource < o.resource)
                return -1;
            else if (this.resource > o.resource)
                return 1;
            return 0;
        }
    }
}
