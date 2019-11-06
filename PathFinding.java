// COMP 3190 Fall 2018 A1Q2
// Yuyi Ding
// Student # 7795699

import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Scanner;

public class PathFinding {
    // global variables
    public static int stateNumber = 0;

    public static void main(String[] args) {
        // get Input
        System.out.println("Please type in puzzle:");
        Scanner scanner = new Scanner(System.in);
        String[] input = scanner.nextLine().split(",");
        int row = Integer.parseInt(input[0]);
        int column = Integer.parseInt(input[1]);
        char[][] world = new char[row][column];
        for(int i=0; i<row; i++){
            String line = scanner.nextLine();
            for(int j=0; j<column; j++){
                world[i][j] = line.charAt(j);
            }
        }

        // calculate costs of all spots
        int[][] costs = new int[world.length][world[0].length];
        for(int i=0; i<world.length; i++){
            for(int j=0; j<world[i].length; j++) {
                if(world[i][j] == '#'){
                    costs[i][j] = -1;
                } else if(world[i][j] == '.') {
                    costs[i][j] = 2;
                } else if(world[i][j] == ':') {
                    costs[i][j] = 3;
                } else if(world[i][j] == '!') {
                    costs[i][j] = 4;
                } else if(world[i][j] == '$') {
                    costs[i][j] = 5;
                } else {
                    costs[i][j] = 1;
                }
            }
        }
        AlgorithmA(world, costs);
    }

    // deep copy a two-dimensional array
    public static char[][] copy(char[][] array) {
        char[][] copy = new char[array.length][array[0].length];
        for (int i = 0; i < array.length; i++) {
            for (int j = 0; j < array[i].length; j++) {
                copy[i][j] = array[i][j];
            }
        }
        return copy;
    }

    // perform Algorithm A to find path
    // g(n) = g(n) of previous state + cost to move to current state,
    // h(n) = distance between current state and target bomb,
    // Compare g(n) + h(n) and put the one with smaller cost to the front of PQueue.
    private static void AlgorithmA(char[][] world, int[][] costs) { ;
        ArrayList<Point> bomb = findBombs(world);
        Point person = findPerson(world);
        int initialTime = 0;
        int disArmed = 0;
        int exploded = 0;
        int cost = 0;
        State currentState = new State(world,bomb.get(0),person);;
        for(int i=0; i<bomb.size(); i++) {
            ArrayList<State> prevState = new ArrayList<>();
            State initialState = new State(world,bomb.get(i),person);
            PriorityQueue<State> states = new PriorityQueue<>();
            states.add(initialState);
            while (!states.isEmpty()) {
                currentState = states.poll();
                while (currentState != null && prevState.size() != 0 && exist(currentState, prevState)) {
                    currentState = states.poll();
                }
                if (currentState != null) {
                    char target = world[bomb.get(i).x][bomb.get(i).y];
                    char A = 'A';
                    int request = (int)target - (int)A + 1;
                    request *= 10;
                    if (currentState.person.compareTo(bomb.get(i)) && + initialTime + currentState.cost <= request) {
                        System.out.println("Bomb disarmed!");
                        currentState.print();
                        world = currentState.world;
                        person = currentState.person;
                        initialTime += currentState.cost;
                        System.out.println(initialTime);
                        cost = initialTime;
                        disArmed++;
                        System.out.print("Bomb disarmed: ");
                        System.out.println(disArmed);
                        System.out.print("Bomb exploded: ");
                        System.out.println(exploded);
                        System.out.print("Cost of the plan: ");
                        System.out.println(cost);
                        System.out.println();
                        break;
                    }
                    prevState.add(currentState);
                    states = getAdjacentStates(currentState, states, costs, bomb.get(i));
                }
                if(states.isEmpty()){
                    exploded ++;
                }
                if(states.size() > 1000000){
                    System.out.println("States out of limits! Search next bomb.");
                    break;
                }
            }
        }
        System.out.println("Final state:");
        currentState.print();
        System.out.print("Bomb disarmed: ");
        System.out.println(disArmed);
        System.out.print("Bomb exploded: ");
        System.out.println(exploded);
        System.out.print("Cost of the plan: ");
        System.out.println(cost);
    }

    // find the person
    private static Point findPerson(char[][] world) {
        for(int i=0; i<world.length; i++){
            for(int j=0; j<world[i].length; j++) {
                if(world[i][j] == '@'){
                    return new Point(i,j);
                }
            }
        }
        return null;
    }

    // find all bombs
    private static ArrayList<Point> findBombs(char[][] world) {
        ArrayList<Point> bombs = new ArrayList<>();
        char target = 'A';
        for(int t=0; t<25; t++) {
            for (int i = 0; i < world.length; i++) {
                for (int j = 0; j < world[i].length; j++) {
                    if (world[i][j] == target) {
                        bombs.add(new Point(i,j));
                    }
                }
            }
            int temp = target + 1;
            target = (char) temp;
        }
        return bombs;
    }

    // check whether a state is checked before
    private static boolean exist(State currentState, ArrayList<State> prevState) {
        for(int i=0; i< prevState.size(); i++) {
            if(currentState.compareWorld(prevState.get(i))){
                return true;
            }
        }
        return false;
    }

    // add all new moves
    private static PriorityQueue<State> getAdjacentStates(State currentState, PriorityQueue<State> states, int[][] costs, Point A) {
        char[][] world = copy(currentState.world);
        int newCostG = costs[currentState.person.x+1][currentState.person.y+1];
        if(newCostG != -1) {
            newCostG += currentState.costG;
            Point newPerson = new Point(currentState.person.x+1, currentState.person.y+1);
            int newCostH = getDistance(A,newPerson);
            State newState = new State(world, newPerson, currentState.person, newCostG, newCostH);
            //newState.print();
            stateNumber++;
            states.add(newState);
        }
        newCostG = costs[currentState.person.x][currentState.person.y+1];
        if(newCostG != -1) {
            newCostG += currentState.costG;
            Point newPerson = new Point(currentState.person.x, currentState.person.y+1);
            int newCostH = getDistance(A,newPerson);
            State newState = new State(world, newPerson, currentState.person, newCostG, newCostH);
            //newState.print();
            states.add(newState);
            stateNumber++;
        }
        newCostG = costs[currentState.person.x-1][currentState.person.y+1];
        if(newCostG != -1) {
            newCostG += currentState.costG;
            Point newPerson = new Point(currentState.person.x-1, currentState.person.y+1);
            int newCostH = getDistance(A,newPerson);
            State newState = new State(world, newPerson, currentState.person, newCostG, newCostH);
            //newState.print();
            states.add(newState);
            stateNumber++;
        }
        newCostG = costs[currentState.person.x+1][currentState.person.y];
        if(newCostG != -1) {
            newCostG += currentState.costG;
            Point newPerson = new Point(currentState.person.x+1, currentState.person.y);
            int newCostH = getDistance(A,newPerson);
            State newState = new State(world, newPerson, currentState.person, newCostG, newCostH);
            //newState.print();
            states.add(newState);
            stateNumber++;
        }
        newCostG = costs[currentState.person.x-1][currentState.person.y];
        if(newCostG != -1) {
            newCostG += currentState.costG;
            Point newPerson = new Point(currentState.person.x-1, currentState.person.y);
            int newCostH = getDistance(A,newPerson);
            State newState = new State(world, newPerson, currentState.person, newCostG, newCostH);
            //newState.print();
            states.add(newState);
            stateNumber++;
        }
        newCostG = costs[currentState.person.x-1][currentState.person.y-1];
        if(newCostG != -1) {
            newCostG += currentState.costG;
            Point newPerson = new Point(currentState.person.x-1, currentState.person.y-1);
            int newCostH = getDistance(A,newPerson);
            State newState = new State(world, newPerson, currentState.person, newCostG, newCostH);
            //newState.print();
            states.add(newState);
            stateNumber++;
        }
        newCostG = costs[currentState.person.x][currentState.person.y-1];
        if(newCostG != -1) {
            newCostG += currentState.costG;
            Point newPerson = new Point(currentState.person.x, currentState.person.y-1);
            int newCostH = getDistance(A,newPerson);
            State newState = new State(world, newPerson, currentState.person, newCostG, newCostH);
            //newState.print();
            states.add(newState);
            stateNumber++;
        }
        newCostG = costs[currentState.person.x+1][currentState.person.y-1];
        if(newCostG != -1) {
            newCostG += currentState.costG;
            Point newPerson = new Point(currentState.person.x+1, currentState.person.y-1);
            int newCostH = getDistance(A,newPerson);
            State newState = new State(world, newPerson, currentState.person, newCostG, newCostH);
            //newState.print();
            states.add(newState);
            stateNumber++;
        }

        return states;
    }

    // get the position of a bomb
    private static Point getBomb(char[][] world, char target) {
        for(int i=0; i<world.length; i++){
            for(int j=0; j<world[i].length; j++) {
                if(world[i][j] == target){
                   return new Point(i,j);
                }
            }
        }
        System.out.println("No bomb " + target);
        return null;
    }

    // get distance between two point
    protected static int getDistance(Point a, Point b){
        return Math.max(Math.abs(a.x - b.x),Math.abs(a.y - b.y));
    }
}

// class State
class State implements Comparable<State> {
    char[][] world;
    Point person;
    int costG;
    int costH;
    int cost;

    // used to create initial state
    public State(char[][] world, Point bomb, Point person) {
        this.world = A1Q2.copy(world);
        this.person = new Point(person.x, person.y);
        costG = 0;
        costH = A1Q2.getDistance(bomb,this.person);
        cost = costG + costH;
    }

    // use to add mew moves
    public State(char[][] world, Point newPerson, Point prevPerson, int newCostG, int newCostH) {
        this.world = A1Q2.copy(world);
        this.person = new Point(newPerson.x, newPerson.y);
        this.costG = newCostG;
        this.costH = newCostH;
        this.putNumber(prevPerson);
        cost = costG + costH;
    }

    // add path to board
    private void putNumber(Point prevPerson) {
        if(Character.isDigit(this.world[person.x][person.y])){
            int temp = (int)this.world[person.x][person.y];
            temp++;
            this.world[person.x][person.y] = (char)temp;
        } else {
            this.world[person.x][person.y] = '1';
        }
    }

    // update '@' icon
    private void upDatePerson(Point newPerson) {
        for(int i=0; i<this.world.length; i++){
            for(int j=0; j<this.world[i].length; j++) {
                if(this.world[i][j] == '@'){
                    this.world[i][j] = ' ';
                    break;
                }
            }
        }
        this.world[newPerson.x][newPerson.y] = '@';
    }

    // compare two world of two different state, check for duplicate.
    public boolean compareWorld(State state) {
        boolean result = true;
        for(int i=0; i<this.world.length; i++){
            for(int j=0; j<this.world[i].length; j++) {
                if(this.person.x != state.person.x || this.person.y != state.person.y){
                    return false;
                } else if(Character.isAlphabetic(world[i][j])){
                    result = result && this.world[i][j] == state.world[i][j];
                }
            }
        }
        return result;
    }

    // determine the position in PQ
    public int compareTo(State state) {
        if(this.cost < state.cost) {
            return -1;
        } else if(this.cost > state.cost) {
            return 1;
        } else {
            return 0;
        }
    }

    // print the state
    public void print() {
        for(int i=0; i<world.length; i++){
            for(int j=0; j<world[i].length; j++) {
                System.out.print(world[i][j]);
            }
            System.out.println();
        }
        System.out.print("State examined: ");
        System.out.println(A1Q2.stateNumber);
    }
}

// class Point
class Point{
    int x;
    int y;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    // compare two points
    public boolean compareTo(Point a){
        if(this.x == a.x && this.y == a.y) {
            return true;
        }
        return false;
    }
}
