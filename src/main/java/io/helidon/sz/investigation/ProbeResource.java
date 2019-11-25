package io.helidon.sz.investigation;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;
import java.util.Objects;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.sql.DataSource;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/probe")
@ApplicationScoped
public class ProbeResource {

    private final DataSource dataSource;
  
    @Inject
    public ProbeResource(@Named("probe") final DataSource dataSource) {
        super();
        this.dataSource = Objects.requireNonNull(dataSource);
    }

    @GET
    @Path("")
    @Produces(MediaType.TEXT_PLAIN)
    public Response probe() {        
        int status = -1;
        try (final Connection connection = this.dataSource.getConnection();
             final Statement statement = connection.createStatement();
             final ResultSet resultSet = statement.executeQuery("SELECT COUNT(TABLE_NAME) FROM INFORMATION_SCHEMA.TABLES")) {
            resultSet.next();
            final int count = resultSet.getInt(1);
            status = count > 0 ? 200 : 500;
        } catch (final SQLException kaboom) {
            kaboom.printStackTrace();
            status = 500;            
        }
        final Response returnValue = Response.status(status).entity(status == 500 ? "unhealthy" : "healthy").build();
        return returnValue;
    }

}
