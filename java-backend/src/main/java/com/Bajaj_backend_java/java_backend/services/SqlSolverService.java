package com.Bajaj_backend_java.java_backend.services;

import org.springframework.stereotype.Service;

@Service
public class SqlSolverService {

    public String solveProblem(String regNo) {
        String digits = regNo.replaceAll("[^0-9]", "");
        int lastDigit = Character.getNumericValue(digits.charAt(digits.length() - 1));

        return getQuestionSolution(); // even
    }
    private String getQuestionSolution() {
        return """
                WITH Qualified AS (
                    SELECT 
                        e.DEPARTMENT AS DEPT_ID,
                        e.EMP_ID,
                        -- Calculate Age using TIMESTAMPDIFF for MySQL compatibility
                        TIMESTAMPDIFF(YEAR, e.DOB, CURDATE()) AS AGE,
                        CONCAT(e.FIRST_NAME, ' ', e.LAST_NAME) AS FULL_NAME
                    FROM EMPLOYEE e
                    JOIN PAYMENTS p ON e.EMP_ID = p.EMP_ID
                    WHERE p.AMOUNT > 70000
                ),
                Ranked AS (
                    -- Rank employees within department to filter top 10 later
                    SELECT 
                        DEPT_ID, 
                        FULL_NAME, 
                        EMP_ID,
                        ROW_NUMBER() OVER (PARTITION BY DEPT_ID ORDER BY EMP_ID) as rn
                    FROM Qualified
                ),
                DeptStats AS (
                    -- Average age of ALL qualified employees
                    SELECT DEPT_ID, AVG(AGE) AS AVG_AGE
                    FROM Qualified
                    GROUP BY DEPT_ID
                ),
                DeptList AS (
                    -- Concatenate only the top 10 employees
                    SELECT 
                        DEPT_ID, 
                        GROUP_CONCAT(FULL_NAME ORDER BY EMP_ID SEPARATOR ', ') AS EMP_LIST
                    FROM Ranked
                    WHERE rn <= 10
                    GROUP BY DEPT_ID
                )
                SELECT 
                    d.DEPARTMENT_NAME,
                    ROUND(ds.AVG_AGE, 2) AS AVERAGE_AGE,
                    dl.EMP_LIST AS EMPLOYEE_LIST
                FROM DEPARTMENT d
                JOIN DeptStats ds ON d.DEPARTMENT_ID = ds.DEPT_ID
                JOIN DeptList dl ON d.DEPARTMENT_ID = dl.DEPT_ID
                ORDER BY d.DEPARTMENT_ID DESC;
                """;
    }
}