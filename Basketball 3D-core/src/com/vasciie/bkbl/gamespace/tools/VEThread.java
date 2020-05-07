package com.vasciie.bkbl.gamespace.tools;

public class VEThread extends Thread {

	Runnable runnable;
	
	boolean run = true, working;

	public static int uses = 0;
	
	public VEThread(Runnable runnable) {
		this.runnable = runnable;

		uses++;
	}

	@Override
	public void run() {
		while(run) {
			runnable.run();

			working = false;
			synchronized(this) {
				try {
					wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	@Override
	public void start() {
		working = true;

		if(getState().equals(State.NEW))
			super.start();
		else {
			synchronized(this) {
				notify();
			}
		}
	}

	@Override
    public void interrupt(){
	    run = false;
        synchronized(this) {
            notify();
        }
    }

    public void waitToFinish(){
		while(working){System.out.println("Waiting");}//An empty while loop freezes the game for some reason
	}
	
}
