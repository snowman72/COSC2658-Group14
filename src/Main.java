import java.util.Scanner;

public class Main {

    // Constants
    static final int N = 8; // number of rows/ columns
    static final long TIME_OUT = 10800000; // 3 hours, but in milliseconds
    static long totalPath = 0;
    static int[] dx = {0, 1, 0, -1}; // direction of moves in x (R, D, L, U)
    static int[] dy = {1, 0, -1, 0}; // direction of moves in y (R, D, L, U)
    static char[] moves; // move characters such as 'U', 'D', 'L', 'R'
    static final long startTime = System.currentTimeMillis();

    public static void main(String[] args) {

        // Sample Input 1: "***************************************************************"
        // Sample Input 2: "*****DR******R******R********************R*D************L******"

        // Get input from user
        Scanner scanner = new Scanner(System.in);
        System.out.print("Input: ");
        String path = scanner.nextLine();

        // Input validation
        if (path.length() != N*N-1 || !path.matches("[UDLR*]*")) {
            // If input is invalid, print error message and not do the search
            System.out.println("Sorry, invalid input.");

        } else {
            // Otherwise, process input and start the search
            moves = path.toCharArray();

            // Create another array to track which cells have been visited
            boolean[][] visited = new boolean[N][N];
            visited[0][0] = true; // starting point needs to be marked as visited

            // Start recursive search
            dfs(0, 0, 0, visited, -1);
        }

        // End timer
        long endTime = System.currentTimeMillis();

        // Print output
        System.out.println("Total paths: " + totalPath);
        System.out.println("Time (ms): " + (endTime - startTime));
    }


    /***
     * Function to search paths by applying the method of depth-first search (dfs) and backtracking algorithm.
     * Base case: We have gone through enough 63 steps and reached the destination index (7,0)
     * @param x row index of current cell
     * @param y column index of current cell
     * @param step number of moves made so far
     * @param visited 2D array to track visited cells
     * @param prevDir previous direction of move
     */
    private static void dfs(int x, int y, int step, boolean[][] visited, int prevDir) {
        long currentTime = System.currentTimeMillis();

        // Check if the search has exceeded the time limit
        if (currentTime - startTime > TIME_OUT) {
            return;
        }

        // Check if we reach destination only after having gone through required number of moves
        // Otherwise, stop current search
        if (step == moves.length) {
            if (x == N - 1 && y == 0) {
                totalPath++;
            }
            return;
        }

        // Get current move direction that has been inputted by user
        char direction = moves[step];
        int[] dirs; // array to store the index of the possible move directions

        if (direction == '*') {
            // If the direction is '*', there are 4 directions to try
            dirs = new int[] {0,1,2,3};

        } else {
            // Get index of the direction inputted by user
            int dirIndex = getDirectionIndex(direction);

            // If invalid direction, stop current search
            if (dirIndex == -1) {
                return;
            }

            // Otherwise, try only the direction inputted by user
            dirs = new int[]{dirIndex};
        }

        for (int dir : dirs) {
            if (prevDir != -1 && dir == (prevDir + 2) % 4) {
                continue; // Avoid U-turns
            }

            // Try calculating the next move
            int nx = x + dx[dir];
            int ny = y + dy[dir];

            // If the move is valid, mark the cell as visited
            if (isValid(nx, ny, visited)) {
                visited[nx][ny] = true;

                // If the remaining unvisited cells cannot together form a valid path,
                // stop current search, and go back to the previous direction
                if (isGridSplit(visited)) {
                    visited[nx][ny] = false; // Backtrack
                    continue;
                }

                dfs(nx, ny, step + 1, visited, dir);

                visited[nx][ny] = false; // Backtrack
            }
        }
    }


    /***
     * Function to check if the move is valid (i.e. within bounds and unvisited)
     * @param x row index of the cell to be moved
     * @param y column index of the cell to be moved
     * @param visited 2D array to track visited cells
     * @return true if the move is valid, false otherwise
     */
    private static boolean isValid(int x, int y, boolean[][] visited) {
        return x >= 0 && y >= 0 && x < N && y < N && !visited[x][y];
    }


    /***
     * Check whether the remaining unvisited cells on the grid cannot together form a valid path
     * (or the grid has been 'split'), by comparing the number of unvisited cells with the number of
     * cells which can form a valid path, with the starting point of iteration is the index of an unvisited
     * cell found in the grid.
     * @param visited 2D array to track visited cells
     * @return true if the remaining unvisited cells cannot together form a valid path, false otherwise
     */
    private static boolean isGridSplit(boolean[][] visited) {
        // Get number of unvisited cells
        int unvisitedCells = countUnvisitedCells(visited);

        if (unvisitedCells == 0) {
            return false;
        }

        boolean[][] visitedCopy = new boolean[N][N];

        // Find an unvisited cell to start counting the number of cells that can be
        // reached from its position by recursively calling dfsCount
        int reachableCells = 0;
        outer: for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if (!visited[i][j]) { // Found an unvisited cell
                    reachableCells = dfsCount(i, j, visitedCopy, visited);
                    break outer;
                }
            }
        }
        return reachableCells != unvisitedCells;
    }


    /***
     * Function to count the number of unvisited cells on the grid
     * @param visited 2D array to track visited cells
     * @return number of unvisited cells
     */
    private static int countUnvisitedCells(boolean[][] visited) {
        int count = 0;
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                // If the cell has not been marked as visited, increment the count
                if (!visited[i][j]) {
                    count++;
                }
            }
        }
        return count;
    }


    /***
     * Function to count the number of cells that can form a valid path, in which
     * this given position index is the starting point. This method uses the method of
     * depth-first search similar to method dfs() above.
     * @param x row index of the cell
     * @param y column index of the cell
     * @param visitedCopy copy of the visited array
     * @param visited 2D array to track visited cells
     * @return number of cells that can form a valid path from the given cell
     */
    private static int dfsCount(int x, int y, boolean[][] visitedCopy, boolean[][] visited) {

        // If the cell is out of bounds, already visited, or marked as visited in the copy array
        if (x < 0 || x >= N || y < 0 || y >= N || visitedCopy[x][y] || visited[x][y]) {
            return 0;
        }

        visitedCopy[x][y] = true;
        int count = 1;

        // Use method depth-first search to count the number of reachable cells
        for (int dir = 0; dir < 4; dir++) {
            count += dfsCount(x + dx[dir], y + dy[dir], visitedCopy, visited);
        }
        return count;
    }


    /**
     * Function that takes in a direction character and return an index,
     * which is based on a particular rule: R -> 0, D -> 1, L -> 2, U -> 3.
     * Any other character will return -1.
     * @param direction character representing the direction
     * @return index of the direction
     */
    private static int getDirectionIndex(char direction) {
        switch (direction) {
            case 'R': return 0;
            case 'D': return 1;
            case 'L': return 2;
            case 'U': return 3;
            default: return -1;
        }
    }
}