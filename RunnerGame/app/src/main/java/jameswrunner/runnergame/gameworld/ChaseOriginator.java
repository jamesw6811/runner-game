package jameswrunner.runnergame.gameworld;

public interface ChaseOriginator {
    void chaseSuccessful();
    void chaseFailed();
    CharSequence getChaseMessage();
}
