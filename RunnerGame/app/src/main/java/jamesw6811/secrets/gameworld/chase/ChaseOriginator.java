package jamesw6811.secrets.gameworld.chase;

public interface ChaseOriginator {
    void chaseSuccessful();
    void chaseFailed();
    CharSequence getChaseMessage();
}
