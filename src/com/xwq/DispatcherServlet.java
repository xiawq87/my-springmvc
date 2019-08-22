package com.xwq;

import com.xwq.handler.MappingHandler;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet(name = "DispatcherServlet", urlPatterns = "/*")
public class DispatcherServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String requestURI = request.getRequestURI();
        Map<String, String[]> parameterMap = request.getParameterMap();

        Object ret = null;
        if(parameterMap != null && parameterMap.size() > 0) {
            Map<String, Object> paramMap = new HashMap<>();

            for(Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
                paramMap.put(entry.getKey(), entry.getValue()[0]);
            }
            ret = MappingHandler.execute(requestURI, paramMap);
        }
        else {
            ret = MappingHandler.execute(requestURI, null);
        }

        response.getWriter().println(ret);
    }
}
