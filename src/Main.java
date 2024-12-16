public class Main {

    // Constants
    static final int N = 8; // number of rows/ columns
    static final int SIZE = 64; // size of grid
    static final long TIME_OUT = 1800000; // 30 minutes

    static boolean isTimedOut = false;
    static long totalPath = 0;
    static int[] dx = {0, 1, 0, -1}; // direction of moves in x (R, D, L, U)
    static int[] dy = {1, 0, -1, 0}; // direction of moves in y (R, D, L, U)
    static char[] moves; // move characters such as 'U', 'D', 'L', 'R'

    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();
//        String path = "***************************************************************"; // Sample Input 1
        String path = "*****DR******R******R********************R*D************L******"; // Sample Input 2
        moves = path.toCharArray();
        boolean[][] visited = new boolean[N][N];
        visited[0][0] = true;
        dfs(0, 0, 0, visited, -1, startTime);
        System.out.println("Last time: " + (System.currentTimeMillis() - startTime) + "ms");
        System.out.println("Total paths: " + totalPath);
    }

    private static void dfs(int x, int y, int step, boolean[][] visited, int prevDir, long startTime) {
        long currentTime = System.currentTimeMillis();

        if (currentTime - startTime > TIME_OUT) {
            isTimedOut = true;
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

        char direction = moves[step];
        int[] dirs;

        if (direction == '*') {
            dirs = new int[] {0,1,2,3};
        } else {
            int dirIndex = getDirectionIndex(direction);
            if (dirIndex == -1) {
                return;
            }
            dirs = new int[]{dirIndex};
        }

        for (int dir : dirs) {
            if (prevDir != -1 && dir == (prevDir + 2) % 4) {
                continue; // Avoid U-turns
            }
            int nx = x + dx[dir];
            int ny = y + dy[dir];
            if (isValid(nx, ny, visited)) {
                visited[nx][ny] = true;
                dfs(nx, ny, step + 1, visited, dir, startTime);
                if (isTimedOut) {
                    return;
                }
                visited[nx][ny] = false; // Backtrack
            }
        }
    }

    private static boolean isValid(int x, int y, boolean[][] visited) {
        return x >= 0 && y >= 0 && x < N && y < N && !visited[x][y];
    }

    private static boolean isTrapped(int x, int y, boolean[][] grid) {
        int numOfBlockDir = 0;
        for (int i = 0; i < 4; i++) {
            int nx = x + dx[i];
            int ny = y + dy[i];
            if (nx < 0 || ny < 0 || nx >= N || ny >= N || grid[nx][ny]) {
                numOfBlockDir++;
            }
        }
        return numOfBlockDir == 4;
    }

    private static boolean isGridSplit(boolean[][] visited) {
        int unvisitedCells = countUnvisitedCells(visited);
        if (unvisitedCells == 0) {
            return false;
        }

        boolean[][] visitedCopy = new boolean[N][N];

        // Find an unvisited cell to start dfsCount
        int reachableCells = 0;
        outer: for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if (!visited[i][j]) {
                    reachableCells = dfsCount(i, j, visitedCopy, visited);
                    break outer;
                }
            }
        }

        return reachableCells != unvisitedCells;
    }

    private static int countUnvisitedCells(boolean[][] visited) {
        int count = 0;
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if (!visited[i][j]) {
                    count++;
                }
            }
        }
        return count;
    }

    private static int dfsCount(int x, int y, boolean[][] visitedCopy, boolean[][] visited) {
        if (x < 0 || x >= N || y < 0 || y >= N || visitedCopy[x][y] || visited[x][y]) {
            return 0;
        }
        visitedCopy[x][y] = true;
        int count = 1;
        for (int dir = 0; dir < 4; dir++) {
            count += dfsCount(x + dx[dir], y + dy[dir], visitedCopy, visited);
        }
        return count;
    }

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