package model;

import contracts.Reportable;
import repository.Repository;

/**
 * Manages team details and aggregates team statistics.
 * Demonstrates Unit II concepts:
 * - Implementation of Reportable interface
 * - Non-static Inner Class (TeamStats) accessing enclosing class members
 * - Meaningful recursion (Unit I) for total hours aggregation
 * - Traditional array traversal and algorithm logic
 */
public class Team implements Reportable {

    private String teamName;
    private Repository<Member> memberRepository;
    private Repository<Task> taskRepository;
    private Repository<Contribution> contributionRepository;
    private TeamStats stats;

    public Team(String teamName, Repository<Member> members, Repository<Task> tasks, Repository<Contribution> contributions) {
        this.teamName = (teamName != null) ? teamName.trim() : "SyncSphere Core Team";
        this.memberRepository = members;
        this.taskRepository = tasks;
        this.contributionRepository = contributions;
        this.stats = new TeamStats();
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = (teamName != null) ? teamName.trim() : "SyncSphere Core Team";
    }

    public TeamStats getStats() {
        return stats;
    }

    /**
     * Non-static inner class for statistical analysis.
     * Demonstrates access to enclosing class instance state (Team.this).
     */
    public class TeamStats {

        /**
         * Calculates total team contribution hours recursively.
         * Demonstrates Unit I syllabus requirement: Meaningful recursion.
         */
        public double calculateTotalHours() {
            Contribution[] contribArray = new Contribution[contributionRepository.size()];
            contribArray = contributionRepository.toArray(contribArray);
            return recursiveSumHours(contribArray, 0);
        }

        /**
         * Recursive helper method that sums hours across an array of contributions.
         * Base case: index >= array length.
         * Recursive step: current element hours + recursive call on next index.
         */
        private double recursiveSumHours(Contribution[] items, int index) {
            if (items == null || index >= items.length) {
                return 0.0;
            }
            return items[index].getHours() + recursiveSumHours(items, index + 1);
        }

        /**
         * Identifies the member with the highest logged contribution hours.
         * Demonstrates standard loop comparison and null safety.
         */
        public Member getTopContributor() {
            if (memberRepository.isEmpty()) {
                return null;
            }
            Member top = null;
            double maxHours = -1.0;
            for (int i = 0; i < memberRepository.size(); i++) {
                Member m = memberRepository.get(i);
                if (m.getTotalHours() > maxHours) {
                    maxHours = m.getTotalHours();
                    top = m;
                }
            }
            return top;
        }

        public int getTotalMembers() {
            return memberRepository.size();
        }

        public int getTotalTasks() {
            return taskRepository.size();
        }

        public int getPendingTasksCount() {
            int count = 0;
            for (int i = 0; i < taskRepository.size(); i++) {
                if (Task.STATUS_PENDING.equalsIgnoreCase(taskRepository.get(i).getStatus())) {
                    count++;
                }
            }
            return count;
        }

        public int getCompletedTasksCount() {
            int count = 0;
            for (int i = 0; i < taskRepository.size(); i++) {
                if (Task.STATUS_COMPLETED.equalsIgnoreCase(taskRepository.get(i).getStatus())) {
                    count++;
                }
            }
            return count;
        }

        public int getApprovedTasksCount() {
            int count = 0;
            for (int i = 0; i < taskRepository.size(); i++) {
                if (Task.STATUS_APPROVED.equalsIgnoreCase(taskRepository.get(i).getStatus())) {
                    count++;
                }
            }
            return count;
        }

        public double getTaskCompletionRate() {
            if (taskRepository.isEmpty()) return 0.0;
            int approved = getApprovedTasksCount();
            return ((double) approved / taskRepository.size()) * 100.0;
        }
    }

    @Override
    public void generateReport() {
        System.out.println("==================================================");
        System.out.println("           " + REPORT_TYPE + ": " + teamName.toUpperCase());
        System.out.println("==================================================");
        System.out.println("Total Members: " + stats.getTotalMembers());
        System.out.println("Total Team Hours: " + String.format("%.2f", stats.calculateTotalHours()));
        Member top = stats.getTopContributor();
        System.out.println("Top Contributor: " + (top != null ? top.getName() + " (" + top.getTotalHours() + " hrs)" : "None"));
        System.out.println("Total Tasks: " + stats.getTotalTasks());
        System.out.println(" - Pending: " + stats.getPendingTasksCount());
        System.out.println(" - Completed Awaiting Approval: " + stats.getCompletedTasksCount());
        System.out.println(" - Approved: " + stats.getApprovedTasksCount());
        System.out.println("Completion Rate: " + String.format("%.1f%%", stats.getTaskCompletionRate()));
        System.out.println("==================================================");
    }
}
