package com.shithead.haodfcrawler.db;

import com.shithead.haodfcrawler.vo.Doctor;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Reader;
import java.util.List;

/**
 * Created by Shulin.kang on 2014/12/15.
 */
public class SimpleDao {
    private static final Logger logger = LoggerFactory.getLogger(SimpleDao.class);
    private static SqlSessionFactory sqlSessionFactory;
    private static Reader reader;
    private static SimpleDao simpleDao;
    private  SimpleDao(){};
    static{
        try{
            reader    = Resources.getResourceAsReader("mybatis.xml");
            sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
        }catch(Exception e){
            e.printStackTrace();
        }
        simpleDao = new SimpleDao();
    }
    public static SimpleDao getInstance(){
        return  simpleDao;
    }
    public List<Doctor> selectDoctorByKind(Doctor doctor){
        SqlSession session = sqlSessionFactory.openSession();
        try{
            return session.selectList("selectValidDoctorByCondition",doctor);
        }catch (Exception e){
            logger.error(e.getMessage());
            return  null;
        }
        finally {
            session.close();
        }
    }

    public Object select(String statement,Object O){
        SqlSession session = sqlSessionFactory.openSession();
        try{
            return session.selectOne(statement,O);
        }catch (Exception e){
            logger.error(e.getMessage());
            return  null;
        }
        finally {
            session.close();
        }
    }
    public void insert(String statement,Object O){
        SqlSession session = sqlSessionFactory.openSession();
        try{
            session.insert(statement,O);
            session.commit();
        }catch (Exception e){
            logger.error(e.getMessage());
        }
        finally {
            session.close();
        }
    }

}
