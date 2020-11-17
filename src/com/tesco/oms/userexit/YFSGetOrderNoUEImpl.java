package com.tesco.oms.userexit;

import com.yantra.interop.util.TransactionWrapper;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSUserExitException;
import com.yantra.yfs.japi.ue.YFSGetOrderNoUE;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

public class YFSGetOrderNoUEImpl implements YFSGetOrderNoUE {

    @Override
    public String getOrderNo(YFSEnvironment yfsEnvironment, Map map) throws YFSUserExitException {
        if (map.get("EnterpriseCode").equals("TESCO GR")) {
            String time = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH).format(new Date());
            Integer orderNo = getOrderNoSequenceNumber();
            return "TGR " + (orderNo == null ? time : orderNo);
        }
        return null;
    }

    private Integer getOrderNoSequenceNumber() {
        InitialContext ctx = null;
        try {
            ctx = new InitialContext();
        } catch (NamingException e) {
            System.out.println("YFSGetOrderNoUEImpl: Exception getting initial context");
        }
        if (ctx == null) {
            return null;
        }
        DataSource ds = null;
        try {
            ds = (DataSource)ctx.lookup("java:jboss/datasources/OracleDS");
        } catch (NamingException e) {
            System.out.println("YFSGetOrderNoUEImpl: Exception getting datasource");
        }
        if (ds == null) {
            return null;
        }
        Connection connection = null;
        try {
            connection = ds.getConnection();
        } catch (SQLException ex) {
            System.out.println("YFSGetOrderNoUEImpl: Error getting db connection");
        }
        if (connection == null) {
            return null;
        }
        PreparedStatement preparedStatement = null;
        String sql = "select ORDERNO_SEQUENCE.nextval as order_no from dual";
        try {
            preparedStatement = connection.prepareStatement(sql);
        } catch (SQLException throwables) {
            System.out.println("YFSGetOrderNoUEImpl: exception while creating prepared statement");
        }
        if (preparedStatement == null) {
            return null;
        }
        ResultSet rs = null;
        try {
             rs = preparedStatement.executeQuery();
        } catch (SQLException throwables) {
            System.out.println("YFSGetOrderNoUEImpl: exception while executing prepared statement");
        }
        if (rs == null) {
            return null;
        }
        Integer orderNo = null;
        try {
            rs.next();
             orderNo = rs.getInt("order_no");
        } catch (SQLException throwables) {
            System.out.println("YFSGetOrderNoUEImpl: exception while parsing resultSet");
        }
        return orderNo;
    }
}
