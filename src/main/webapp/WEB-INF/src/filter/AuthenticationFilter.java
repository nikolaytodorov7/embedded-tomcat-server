package filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.StatusMessage;

import java.io.IOException;

import static util.ServletUtility.buildPath;
import static util.ServletUtility.writeAsJson;

public class AuthenticationFilter extends HttpFilter {
    public void doFilter(HttpServletRequest req, HttpServletResponse resp, FilterChain chain) throws IOException, ServletException {
        System.out.println("PATH: " + buildPath(req));
        System.out.println("PATH INFO: " + req.getPathInfo());
        System.out.println("SERVLET PATH: " + req.getServletPath());
        System.out.println("QUERY String: " + req.getQueryString());


        HttpSession session = req.getSession(false);
        if (session == null) {
            StatusMessage msg = new StatusMessage(401, "There is no active session, please log in.");
            writeAsJson(resp, msg);
            return;
        }

        chain.doFilter(req, resp);
    }
}
