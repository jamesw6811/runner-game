package jamesw6811.secrets.gameworld;

public interface ChaseOriginator {
    void chaseSuccessful();
    void chaseFailed();
    CharSequence getChaseMessage();
}
