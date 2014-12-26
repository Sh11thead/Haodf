package com.shithead.haodfcrawler.thread;

import com.shithead.haodfcrawler.thread.task.HyhTask;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * Created by Shulin.kang on 2014/12/26.
 */
public class ThreadServ {
    private static ThreadServ threadServ = new ThreadServ();
    private ExecutorService es = null;
    private CompletionService<String> completionService;
    public static ThreadServ getInstance(){
        return threadServ;
    }
    private ThreadServ(){
        this.es = Executors.newFixedThreadPool(100);
        this.completionService = new ExecutorCompletionService<String>(es);
    }

    public List<Integer> runAndgetConsult(List<HyhTask> list){
        for(HyhTask t:list){
            completionService.submit(t);
        }
        try{
            Integer allRes =0;
            Integer allQues =0;
            for(int i=0;i<list.size();i++){
                Future<String> future = completionService.take();
                if(future!=null){
                    String s = future.get();
                    if(s!=null){
                        String[] yihuanarray = s.split(",");
                        if(yihuanarray.length==2){
                            try{
                                allRes+=Integer.valueOf(yihuanarray[0]);
                                allQues+=Integer.valueOf(yihuanarray[1]);
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
            List<Integer> ret =new ArrayList<Integer>();
            ret.add(allRes);
            ret.add(allQues);
            return ret;
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
