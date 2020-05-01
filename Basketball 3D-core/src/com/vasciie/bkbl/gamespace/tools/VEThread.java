package com.vasciie.bkbl.gamespace.tools;

public class VEThread extends Thread {

	Runnable runnable;
	
	boolean run = true;
	
	public VEThread(Runnable runnable) {
		this.runnable = runnable;
	}

	@Override
	public void run() {
		while(run) {
			runnable.run();
			
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
	
}
