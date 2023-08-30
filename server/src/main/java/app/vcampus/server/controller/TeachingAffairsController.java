package app.vcampus.server.controller;


import app.vcampus.server.entity.Class;
import app.vcampus.server.entity.Course;
import app.vcampus.server.entity.User;
import app.vcampus.server.utility.Request;
import app.vcampus.server.utility.Response;
import app.vcampus.server.utility.router.RouteMapping;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Transaction;

import java.util.concurrent.TransferQueue;

@Slf4j
public class TeachingAffairsController {
    @RouteMapping(uri = "course/addCourse")
    public Response addCourse(Request request, org.hibernate.Session database)
    {
        Course newCourse=Course.fromMap(request.getParams());
        if(newCourse==null)
        {
            return Response.Common.badRequest();
        }

        Course user=database.get(Course.class,newCourse.getCourseId());
        if(user==null){
            return Response.Common.error("Course not found");
        }

        Transaction tx=database.beginTransaction();
        database.persist(newCourse);
        tx.commit();

        return Response.Common.ok();
    }

//    @RouteMapping(uri="class/searchClass")
//    public Response searchClass(Request request,org.hibernate.Session database)
//    {
//        String courseId=request.getParams().get("courseId");
//        if(courseId==null){
//            return Response.Common.error("course id could not be empty");
//        }
//
//        Class class
//    }

    @RouteMapping(uri = "class/addClass")
    public  Response addClass(Request request,org.hibernate.Session database)
    {
        Class newclass= Class.fromMap(request.getParams());
        if(newclass==null)return Response.Common.badRequest();

        User user=database.get(User.class,newclass.getCourseId());
        if(user==null){
            return Response.Common.error("not found");
        }

        Transaction tx=database.beginTransaction();
        database.persist(newclass);
        tx.commit();

        return Response.Common.ok();
    }







}
