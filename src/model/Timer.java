package model;

public class Timer extends Thread{
    public boolean check = false;

    @Override
    public void run(){
        try {
            Thread.sleep(10000);
            check = true;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
