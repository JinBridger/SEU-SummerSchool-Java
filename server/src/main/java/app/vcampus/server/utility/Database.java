package app.vcampus.server.utility;

import app.vcampus.server.entity.*;
import jakarta.persistence.criteria.*;
import org.hibernate.Session;
import org.hibernate.cfg.Configuration;

import java.util.ArrayList;
import java.util.List;

public class Database {
    public static Session init() {
        Configuration configuration = new Configuration().configure();
        return configuration
                .addAnnotatedClass(User.class)
                .addAnnotatedClass(Student.class)
                .addAnnotatedClass(Course.class)
                .addAnnotatedClass(LibraryBook.class)
                .addAnnotatedClass(TeachingClass.class)
                .addAnnotatedClass(StoreItem.class)
                .addAnnotatedClass(StoreTransaction.class)
                .addAnnotatedClass(SelectedClass.class)
                .addAnnotatedClass(FinanceCard.class)
                .addAnnotatedClass(CardTransaction.class)
                .buildSessionFactory().openSession();
    }

    public static <T> List<T> loadAllData(Class<T> type, Session session) {
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<T> criteria = builder.createQuery(type);
        criteria.from(type);
        return session.createQuery(criteria).getResultList();
    }

    public static <T> List<T> likeQuery(Class<T> type, String[] field, String value, Session session) {
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<T> criteria = builder.createQuery(type);
        Root<T> itemRoot = criteria.from(type);
        ArrayList<Predicate> conditions = new ArrayList<>();
        for (String s : field) {
            conditions.add(builder.like(itemRoot.get(s).as(String.class), "%" + value + "%"));
        }
        criteria.where(builder.or(conditions.toArray(new Predicate[0])));
        return session.createQuery(criteria).getResultList();
    }

    public static <T> List<T> getWhere(Class<T> type, String field, String value, Session session) {
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<T> criteria = builder.createQuery(type);
        Root<T> itemRoot = criteria.from(type);
        criteria.where(builder.equal(itemRoot.get(field).as(String.class), value));
        return session.createQuery(criteria).getResultList();
    }

    public static <T> void updateWhere(Class<T> type, String field, String value, List<Pair<String, String>> updates, Session session) {
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaUpdate<T> criteria = builder.createCriteriaUpdate(type);
        Root<T> itemRoot = criteria.from(type);

        criteria.where(builder.equal(itemRoot.get(field).as(String.class), value));
        updates.forEach(pair -> criteria.set(pair.getFirst(), pair.getSecond()));

        session.createMutationQuery(criteria).executeUpdate();
    }
}
