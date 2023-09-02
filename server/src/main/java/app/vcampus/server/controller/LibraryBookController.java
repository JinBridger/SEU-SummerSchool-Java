package app.vcampus.server.controller;

import app.vcampus.server.entity.IEntity;
import app.vcampus.server.entity.LibraryBook;
import app.vcampus.server.entity.LibraryTransaction;
import app.vcampus.server.entity.Student;
import app.vcampus.server.utility.Database;
import app.vcampus.server.utility.Request;
import app.vcampus.server.utility.Response;
import app.vcampus.server.utility.router.RouteMapping;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Transaction;

import java.lang.reflect.Type;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
public class LibraryBookController {

    @RouteMapping(uri = "library/addBook", role = "library_staff")
    public Response addBook(Request request, org.hibernate.Session database) {
        LibraryBook newBook = IEntity.fromJson(request.getParams().get("book"), LibraryBook.class);
        if (newBook == null) {
            return Response.Common.badRequest();
        }

        newBook.setUuid(UUID.randomUUID());
        Transaction tx = database.beginTransaction();
        database.persist(newBook);
        tx.commit();

        return Response.Common.ok();
    }

//    @RouteMapping(uri = "library/deleteBook")
//    public Response deleteBook(Request request, org.hibernate.Session database) {
//        String UUID = request.getParams().get("UUID");
//
//        if (UUID == null) return Response.Common.error("Book UUID cannot be empty");
//
//        LibraryBook toDelete = database.get(LibraryBook.class, UUID);
//        if (toDelete == null) return Response.Common.error("No such book");
//
//        Transaction tx = database.beginTransaction();
//        database.remove(toDelete);
//        tx.commit();
//
//        return Response.Common.ok();
//    }
//
//    @RouteMapping(uri = "library/updateBook")
//    public Response updateBook(Request request, org.hibernate.Session database) {
//        LibraryBook newBook = LibraryBook.fromrequest(request);
//        if (newBook == null) return Response.Common.badRequest();
//
//        LibraryBook toUpdate = database.get(LibraryBook.class, request.getParams().get("UUID"));
//        if (toUpdate == null) return Response.Common.error("Incorrect book UUID");
//
//        if (newBook == toUpdate) return Response.Common.error("No update");
//
//        Transaction tx = database.beginTransaction();
//        toUpdate.setBookStatus(newBook.getBookStatus());
//        toUpdate.setDescription(newBook.getDescription());
//        toUpdate.setPlace(newBook.getPlace());
//        database.persist(toUpdate);
//        tx.commit();
//
//        return Response.Common.ok();
//
//    }
//
    @RouteMapping(uri = "library/searchBook")
    public Response searchBook(Request request, org.hibernate.Session database) {
        try {
            String keyword = request.getParams().get("keyword");
            if (keyword == null) return Response.Common.error("Keyword cannot be empty");
            List<LibraryBook> books = Database.likeQuery(LibraryBook.class, new String[]{"name", "isbn", "author", "description", "press"}, keyword, database);

            return Response.Common.ok(books.stream().collect(Collectors.groupingBy(w -> w.isbn)).entrySet().stream().collect(Collectors.toMap(
                    Map.Entry::getKey,
                    e -> e.getValue().stream().map(LibraryBook::toJson).collect(Collectors.toList())
            )));
        } catch (Exception e) {
            return Response.Common.error("Failed to search students");
        }
    }

    @RouteMapping(uri = "library/isbn", role = "library_staff")
    public Response isbn(Request request, org.hibernate.Session database) {
        String isbn = request.getParams().get("isbn");

        if (isbn == null) return Response.Common.error("ISBN cannot be empty");

        List<LibraryBook> searchedBook = Database.getWhere(LibraryBook.class, "isbn", isbn, database);
        if (!searchedBook.isEmpty()) return Response.Common.ok(Map.of("book", searchedBook.get(0).toJson()));

        try {
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(java.net.URI.create("http://47.99.80.202:6066/openApi/getInfoByIsbn?appKey=ae1718d4587744b0b79f940fbef69e77&isbn=" + isbn))
                    .GET()
                    .build();

            HttpResponse result = HttpClient.newHttpClient().send(httpRequest, java.net.http.HttpResponse.BodyHandlers.ofString());
            log.info(result.toString());
            Type type = new TypeToken<Map<String, Object>>(){}.getType();
            Map<String, Object> data = (new Gson()).fromJson(result.body().toString(), type);
            data = (Map<String, Object>) data.get("data");
            LibraryBook newBook = LibraryBook.fromWeb(data);
            return Response.Common.ok(Map.of("book", newBook.toJson()));
        } catch (Exception e) {
            e.printStackTrace();
            return Response.Common.error("Failed to get book info");
        }
    }

    @RouteMapping(uri="library/getBookInfo")
    public Response getBookInfo(Request request,org.hibernate.Session database){
        /*
        this method is used when user clicks the searched book to show the detailed book information
         */
        String id=request.getParams().get("uuid");
        if(id==null) return Response.Common.error("uuid cannot be empty");

        UUID uuid=UUID.fromString(id);
        LibraryBook book=database.get(LibraryBook.class,uuid);

        if(book==null){
            return Response.Common.error("missing book information");
        }

        return Response.Common.ok(book.toMap());
    }


}
