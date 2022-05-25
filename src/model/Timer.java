package model;

public class Timer extends Thread{
    public boolean check = false;

    @Override
    public void run(){
        for (int i = 10 ; i > 0 ; i--){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        check = true;
    }
}
