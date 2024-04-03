package modele;

import java.lang.reflect.Method;
import java.util.ArrayList;

/**
 * A simple class that follows a datastructure of a type, through numbered commits
 * Possibility to go back in time, to replace older commits (thus removing everything after)
 */
public class CommitsFollower<T> {
    private ArrayList<T> tab = new ArrayList<T>();
    
    public CommitsFollower() {}

    /**
     * 
     * @param commitId : the id of the commit
     * @return A reference to a 
    */
    public T getCommit(int commitId) throws IndexOutOfBoundsException {
        if (commitId < 0 || commitId > tab.size() - 1) {
            throw new IndexOutOfBoundsException("Commits range from 0 to " + (tab.size() - 1));
        }
        return tab.get(commitId);
    }

    public int currentCommitId() {
        return tab.size() - 1;
    }

    public void addCommit(int commitId, T value) throws IndexOutOfBoundsException {
        T cloneValue;
        try {
            cloneValue = this.cloneObject(value);
        } catch (CloneNotSupportedException e){
            System.err.println("Error while cloning a commit");
            return;
        }
        
    
        if (commitId > tab.size()) {
            throw new IndexOutOfBoundsException("You can only add a commit right next to the previous one or in the current tab");
        }
        if (commitId == tab.size()) {
            tab.addLast(cloneValue);
        } else {
            tab.set(commitId, cloneValue);
        }
        for (int i = tab.size() - 1; i > commitId; i--) {
            tab.remove(i);
        }
    }

    // Utility method to clone an object
    private T cloneObject(T original) throws CloneNotSupportedException {
        if (original instanceof String) {
            return original;
        }
        try {
            Method cloneMethod = original.getClass().getMethod("clone");
            return (T) cloneMethod.invoke(original);
        } catch (Exception e) {
            throw new CloneNotSupportedException("Cloning not supported");
        }
    }
}
