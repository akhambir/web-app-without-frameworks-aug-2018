package com.akhambir.dao;

import com.akhambir.model.Category;
import com.akhambir.model.Product;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class CategoryDaoImpl extends AbstractDao<Category> implements CategoryDao {

    public CategoryDaoImpl(Connection connection) {
        super(connection);
    }

    @Override
    public Category findById(Long id) {
        String query = "SELECT C.ID, C.CATEGORY_NAME, P.ID, P.NAME, P.PRICE, P.DESCRIPTION FROM CATEGORIES C " +
                "JOIN PRODUCTS P ON C.ID = P.FK_CATEGORIES " +
                "WHERE C.ID = ?";

        PreparedStatement statement;
        ResultSet resultSet;
        Category result = null;

        try {
            statement = connection.prepareStatement(query);
            statement.setLong(1, id);
            resultSet = statement.executeQuery();
            result = resultSet.next() ? getCategory(resultSet) : null;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public List<Category> findAll() {
        String query = "SELECT ID, CATEGORY_NAME FROM CATEGORIES";
        Statement statement = null;
        ResultSet resultSet = null;
        List<Category> categories = new ArrayList<>();

        try {
            statement = connection.createStatement();
            resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                Category category = new Category(
                        resultSet.getLong(1),
                        resultSet.getString(2),
                        null
                );
                categories.add(category);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return categories;
    }

    private Category getCategory(ResultSet resultSet) throws SQLException {
        List<Product> products = new ArrayList<>();
        Category result = new Category(
                resultSet.getLong(1),
                resultSet.getString(2),
                products);

        while (!resultSet.isAfterLast()) {
            Product product = new Product(
                    resultSet.getLong(3),
                    resultSet.getString(4),
                    resultSet.getDouble(5),
                    resultSet.getString(6)
            );
            products.add(product);
            resultSet.next();
        }

        return result;
    }
}
