package model;

import java.util.Comparator;

public class EventPriorityComparator implements Comparator<Event> {
    @Override
    public int compare(Event e1, Event e2) {
        // Make HIGH priority appear first, LOW priority at the end
        // First compare priority, if same then compare startTime
        int p1 = e1.getPriority().ordinal(); // LOW=0,MEDIUM=1,HIGH=2
        int p2 = e2.getPriority().ordinal();
        // Want HIGH to be first => larger ordinal at front => return negative value
        // Can be written as: return Integer.compare(p2, p1); 
        int priorityCompare = p2 - p1; 
        if (priorityCompare != 0) {
            return priorityCompare;
        }
        // If priority is the same, sort by earlier start time first
        return e1.getStartTime().compareTo(e2.getStartTime());
    }
}
