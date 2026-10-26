<%@ page contentType="text/html;charset=UTF-8" language="java" %>
        </div> <!-- End of content-wrapper -->
        <footer style="padding: 16px 32px; background: #ffffff; border-top: 1px solid #e2e8f0; font-size: 13px; color: #64748b; display: flex; justify-content: space-between; align-items: center; flex-wrap: wrap; gap: 10px;">
            <div>
                &copy; <%= java.time.Year.now().getValue() %> <strong>Employee Leave Management System</strong>. All rights reserved.
            </div>
            <div>
                <span>Powered by Jakarta EE 10 &bull; Java 21 &bull; MySQL</span>
            </div>
        </footer>
    </div> <!-- End of main-content -->
</div> <!-- End of app-layout -->

<script src="<%= request.getContextPath() %>/js/main.js"></script>
</body>
</html>
