package ru.itis.serlets;

import ru.itis.models.User;
import ru.itis.repository.UsersRepository;
import ru.itis.repository.UsersRepositoryJdbcImpl;
import ru.itis.services.UsersService;
import ru.itis.services.UsersServiceImpl;
import sun.jvm.hotspot.gc.shared.CollectedHeapName;
import tools.jackson.databind.ObjectMapper;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/ajax")
public class AjaxServlet extends HttpServlet {

    private static final String DB_USERNAME = "postgres";
    private static final String DB_PASSWORD = "gjhfqr102";
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/testdb_11_503";


    private UsersRepository usersRepository;
    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/html/ajax_example.html").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = objectMapper.readValue(req.getReader(), User.class);
//        System.out.println(user.getName());
        try {
            usersRepository.save(user);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
//        System.out.println("*****");
        List<User> users;
        try {
            users = usersRepository.findAll();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        String usersAsJson = objectMapper.writeValueAsString(users);
//        System.out.println(usersAsJson.getBytes());
        resp.setContentType("application/json");
        resp.getWriter().println(usersAsJson);
    }

    @Override
    public void init() throws ServletException {
        try {
            Class.forName("org.postgresql.Driver");
            Connection connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
            usersRepository = new UsersRepositoryJdbcImpl(connection);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
