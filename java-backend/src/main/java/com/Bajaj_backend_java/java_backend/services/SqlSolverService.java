package com.Bajaj_backend_java.java_backend.services;
import org.springframework.stereotype.Service;

@Service
public class SqlSolverService {

    public String solveProblem(String regNo) {
        // Extract numbers from regNo (e.g., 22BCE0520 -> 220520)
        String digits = regNo.replaceAll("[^0-9]", "");

        // Take the last digit to check parity
        int lastDigit = Character.getNumericValue(digits.charAt(digits.length() - 1));

        if (lastDigit % 2 != 0) {
            return getQuestion1Solution(); // Odd
        } else {
            return getQuestion2Solution(); // Even (Your case)
        }
    }

    private String getQuestion1Solution() {
        // Placeholder for Odd registration numbers
        return "SELECT * FROM QUESTION_1_SOLUTION_ODD";
    }

    private String getQuestion2Solution() {
        // SQL Solution for EVEN registration numbers (Your Case)
        // Problem: Avg age of employees with salary > 70k, concat names, ordered by Dept ID.
        return """
            WITH QualifiedEmployees AS (
                -- 1. Select employees who earned more than 70000
                SELECT
                    e.EMP_ID,
                    e.FIRST_NAME,
                    e.LAST_NAME,
                    e.DEPARTMENT AS DEPARTMENT_ID,
                    p.AMOUNT,
                    -- Calculate Age (Assumes standard MySQL/Postgres date functions)
                    TIMESTAMPDIFF(YEAR, e.DOB, CURDATE()) AS AGE
                FROM
                    EMPLOYEE e
                JOIN
                    PAYMENTS p ON e.EMP_ID = p.EMP_ID
                WHERE
                    p.AMOUNT > 70000
            )
            SELECT
                d.DEPARTMENT_NAME,
                -- 2. Calculate the average age of the qualified employees in that department
                FORMAT(AVG(qe.AGE), 2) AS AVERAGE_AGE, 
                -- 3. Create a comma-separated list of up to 10 qualified employee names
                (
                    SELECT
                        GROUP_CONCAT(CONCAT(t.FIRST_NAME, ' ', t.LAST_NAME)
                                     ORDER BY t.EMP_ID 
                                     SEPARATOR ', ')
                    FROM
                        QualifiedEmployees t
                    WHERE
                        t.DEPARTMENT_ID = d.DEPARTMENT_ID
                    LIMIT 10
                ) AS EMPLOYEE_LIST
            FROM
                DEPARTMENT d
            LEFT JOIN
                QualifiedEmployees qe ON d.DEPARTMENT_ID = qe.DEPARTMENT_ID
            GROUP BY
                d.DEPARTMENT_ID, d.DEPARTMENT_NAME
            ORDER BY
                d.DEPARTMENT_ID DESC;
            """;
    }
}