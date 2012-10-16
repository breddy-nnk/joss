package nl.tweeenveertig.openstack.command.identity.access;

import nl.tweeenveertig.openstack.command.core.CommandExceptionError;
import nl.tweeenveertig.openstack.exception.HttpStatusToExceptionMapper;
import org.apache.http.HttpStatus;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.annotate.JsonRootName;

import java.util.ArrayList;
import java.util.List;

@JsonRootName(value="access")
public class Access {

    public static final String SERVICE_CATALOG_OBJECT_STORE = "object-store";
    public Token token;

    public List<ServiceCatalog> serviceCatalog = new ArrayList<ServiceCatalog>();

    public User user;

    @JsonIgnore
    private EndPoint currentEndPoint;

    public void setPreferredRegion(String preferredRegion) {
        this.currentEndPoint = getObjectStoreCatalog().getRegion(preferredRegion);
    }

    public String getToken() {
        return token == null ? null : token.id;
    }

    public ServiceCatalog getObjectStoreCatalog() {
        for (ServiceCatalog catalog : serviceCatalog) {
            if (SERVICE_CATALOG_OBJECT_STORE.equals(catalog.type)) {
                return catalog;
            }
        }
        return null;
    }

    @SuppressWarnings("ConstantConditions")
    public Access initCurrentEndPoint() {
        ServiceCatalog objectStoreCatalog = getObjectStoreCatalog();
        if (objectStoreCatalog == null) {
            HttpStatusToExceptionMapper.throwException(HttpStatus.SC_NOT_FOUND, CommandExceptionError.NO_SERVICE_CATALOG_FOUND);
        }
        this.currentEndPoint = objectStoreCatalog.getRegion(null);
        if (this.currentEndPoint == null) {
            HttpStatusToExceptionMapper.throwException(HttpStatus.SC_NOT_FOUND, CommandExceptionError.NO_END_POINT_FOUND);
        }
        return this;
    }

    public String getInternalURL() {
        return currentEndPoint.internalURL;
    }

    public String getPublicURL() {
        return currentEndPoint.publicURL;
    }

}
