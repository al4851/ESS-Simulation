import java.util.*;

/**
 * Simulate a number of individuals in a population.
 * Individuals will adopt different strategies when engaged
 * in conflict with other members of the same species.
 * There will be two strategies that are tested: "Hawks" and "Doves".
 * Usage: Simulation popSize [percentHawks] [resourceAmt] [costHawk-Hawk]
 *
 * @author Alfred Li
 */
public class Simulation {
    public static void main(String[] args) {
        if (args.length == 0 || args.length > 4) {
            System.err.println("Usage: Simulation popSize [percentHawks] [resourceAmt] [costHawk-Hawk]");
            System.exit(0);
        } else {
            List<Individual> individuals = create(args);
            run(individuals);
        }
    }

    /**
     * Main flow of program, perform option based on user input
     * @param individuals list of individuals
     */
    public static void run(List<Individual> individuals) {
        Scanner scan = new Scanner(System.in);
        int input = 0;
        while (input != 8) {
            menu();
            // Read user option
            input = Integer.parseInt(scan.nextLine());
            System.out.println();
            // Validate option
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
                // Perform 1000 interactions
                case 4 -> interactions(individuals, 1000);
                // Perform 10000 interactions
                case 5 -> interactions(individuals, 10000);
                // Perform number of interactions based on user input
                case 6 -> {
                    System.out.println("Enter the number of interaction");
                    System.out.print("> ");
                    int num = Integer.parseInt(scan.nextLine());
                    interactions(individuals, num);
                }
                // Perform interactions one at a time until user enter Stop
                case 7 -> {
                    String s = "";
                    while (!(s.equals("Stop"))) {
                        interactions(individuals, 1);
                        s = scan.nextLine();
                    }
                }
                // Quit program
                case 8 -> System.out.print("Quit");
            }
        }
    }

    /**
     * Create a list of individuals based on argument parameter
     * @param args argument parameter for creation
     * @return list of individuals
     */
    public static List<Individual> create(String[] args) {
        Individual.population = Integer.parseInt(args[0]);
        Individual.hawkPercent = 20;
        Individual.resourceAmount = 50;
        Individual.hawkPenalty = 100;
        Individual.living = Individual.population;
        Individual.encounter = 0;
        // Update information if argument provided
        if (args.length == 2)
            Individual.hawkPercent = Integer.parseInt(args[1]);
        if (args.length == 3)
            Individual.resourceAmount = Integer.parseInt(args[2]);
        if (args.length == 4)
            Individual.hawkPenalty = Integer.parseInt(args[3]);

        // Create list of individuals
        List<Individual> individuals = new ArrayList<>();
        int numOfHawks = Individual.population * Individual.hawkPercent / 100;
        for (int i = 0; i < numOfHawks; i++) {
            individuals.add(new Individual(i, "Hawk"));
        }
        for (int i = numOfHawks; i < Individual.population; i++) {
            individuals.add(new Individual(i, "Dove"));
        }
        return individuals;
    }

    /**
     * Item "1" in menu to display the starting statistics.
     */
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
        System.out.println();
    }

    /**
     * Item "2" in menu to display individuals information in the population
     * @param individuals list of individuals
     */
    public static void displayInfo(List<Individual> individuals) {
        for (Individual i : individuals) {
            System.out.printf("Individual[%d]=%s:%d\n", i.id, i.strategy, i.resource);
        }
        System.out.println("Living: " + Individual.living);
        System.out.println();
    }

    /**
     * Item "3" in menu to display sorted individuals' information in the population
     * @param individuals list of individuals
     */
    public static void sortedInfo(List<Individual> individuals) {
        List<SIndividual> clone = new ArrayList<>(individuals.size());
        for (Individual i : individuals) {
            clone.add(new SIndividual(i.strategy, i.resource));
        }
        clone.sort(Collections.reverseOrder());
        for (SIndividual i : clone) {
            System.out.printf("%s:%d\n", i.strategy, i.resource);
        }
        System.out.println();
    }

    /**
     * Perform multiple interactions
     * @param individuals list of individuals
     * @param num number of interaction to perform
     */
    public static void interactions(List<Individual> individuals, int num) {
        for (int i = 0; i < num; i++) {
            if (Individual.living < 2) {
                System.out.println("Living: " + Individual.living);
                System.out.println("Unable to perform interactions");
                break;
            }
            Individual.encounter++;
            System.out.println("Encounter: " + Individual.encounter);
            interaction(individuals);
        }
    }

    /**
     * Randomly select two individuals to perform interaction base on strategy
     * @param individuals list of individuals
     */
    public static void interaction(List<Individual> individuals) {
        Random random = new Random();
        // Pick first individual that is not dead
        int first = random.nextInt(individuals.size());
        while (individuals.get(first).isDead) {
            first = random.nextInt(individuals.size());
        }
        // Pick second individual that is not dead and not the same as first
        int second = random.nextInt(individuals.size());
        while (individuals.get(second).isDead || first == second) {
            second = random.nextInt(individuals.size());
        }
        // Perform interaction based on strategy
        Individual one = individuals.get(first);
        Individual two = individuals.get(second);
        if (one.strategy.equals("Dove") && two.strategy.equals("Dove")) {
            doveDove(one, two);
        } else if (one.strategy.equals("Hawk") && two.strategy.equals("Dove")) {
            hawkDove(one, two);
        } else if (one.strategy.equals("Dove") && two.strategy.equals("Hawk")) {
            hawkDove(two, one);
        } else if (one.strategy.equals("Hawk") && two.strategy.equals("Hawk")) {
            hawkHawk(one, two);
        }
        System.out.println();
    }

    /**
     * Dove-Dove interaction
     * Split the resource equally to both individual
     * @param one first individual
     * @param two second individual
     */
    public static void doveDove(Individual one, Individual two) {
        System.out.printf("Individual %d: %s\n", one.id, one.strategy);
        System.out.printf("Individual %d: %s\n", two.id, two.strategy);
        one.resource += Individual.resourceAmount / 2;
        two.resource += Individual.resourceAmount / 2;
        System.out.printf("%s/%s: %s: +%d\t%s: +%d\n",
                one.strategy, two.strategy,
                one.strategy, Individual.resourceAmount / 2,
                two.strategy, Individual.resourceAmount / 2);
        System.out.printf("Individual %d=%d\t\t\tIndividual %d=%d\n",
                one.id, one.resource,
                two.id, two.resource);
    }

    /**
     * Hawk-Dove interaction
     * Hawk takes all resource
     * Dove gets nothing, but not injured
     * @param one first individual
     * @param two second individual
     */
    public static void hawkDove(Individual one, Individual two) {
        System.out.printf("Individual %d: %s\n", one.id, one.strategy);
        System.out.printf("Individual %d: %s\n", two.id, two.strategy);
        one.resource += Individual.resourceAmount;
        System.out.printf("%s/%s: %s: +%d\t%s: +0\n",
                one.strategy, two.strategy,
                one.strategy, Individual.resourceAmount,
                two.strategy);
        System.out.printf("Individual %d=%d\t\t\tIndividual %d=%d\n",
                one.id, one.resource,
                two.id, two.resource);
    }

    /**
     * Hawk-Hawk interaction
     * First Hawk individual take all resource
     * Then both individual bear the cost of Hawk interaction
     * If individual resource below 0, individual dies
     * @param one first individual
     * @param two second individual
     */
    public static void hawkHawk(Individual one, Individual two) {
        System.out.printf("Individual %d: %s\n", one.id, one.strategy);
        System.out.printf("Individual %d: %s\n", two.id, two.strategy);
        int resource = Individual.hawkPenalty - Individual.resourceAmount;
        one.resource -= resource;
        two.resource -= Individual.hawkPenalty;
        System.out.printf("%s/%s: %s: -%d\t%s: -%d\n",
                one.strategy, two.strategy,
                one.strategy, resource,
                two.strategy, Individual.hawkPenalty);
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

    /**
     * Display menu options
     */
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

    /**
     * Class to store individual information
     */
    static class Individual {
        // number of population
        static int population;
        // hawk percentage in population
        static int hawkPercent;
        // resource amount when competing interactions
        static int resourceAmount;
        // penalty amount when Hawk-Hawk encounters
        static int hawkPenalty;
        // number of individuals still living
        static int living;
        // number of encounter interactions
        static int encounter;

        // individual id
        int id;
        // individual current resource amount
        int resource;
        // individual strategy
        String strategy;
        // individual living status
        boolean isDead;

        /**
         * Constructor for individual
         * @param id individual id
         * @param strategy individual strategy
         */
        public Individual(int id, String strategy) {
            this.id = id;
            this.resource = 0;
            this.strategy = strategy;
            this.isDead = false;
        }

        /**
         * Update individual to dead
         */
        public void dead() {
            this.strategy = "DEAD";
            this.isDead = true;
            living--;
        }
    }

    /**
     * Class for temporary individual for sort operation
     */
    static class SIndividual implements Comparable<SIndividual>{
        // individual strategy
        String strategy;
        // individual current resource amount
        int resource;

        /**
         * Constructor for sorted individual
         * @param strategy individual strategy
         * @param resource individual current resource amount
         */
        public SIndividual(String strategy, int resource) {
            this.strategy = strategy;
            this.resource = resource;
        }

        /**
         * Comparator for to compare two individual
         * @param o the other individual
         * @return the order of two individual
         */
        @Override
        public int compareTo(SIndividual o) {
            if (this.resource < o.resource)
                return -1;
            else if (this.resource > o.resource)
                return 1;
            return 0;
        }
    }
}
