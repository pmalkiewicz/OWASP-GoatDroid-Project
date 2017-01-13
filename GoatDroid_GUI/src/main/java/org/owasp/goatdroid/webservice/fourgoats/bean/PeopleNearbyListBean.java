package org.owasp.goatdroid.webservice.fourgoats.bean;

import org.owasp.goatdroid.webservice.fourgoats.model.NearbyUserModel;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class PeopleNearbyListBean extends BaseBean{
    ArrayList<NearbyUserModel> users;

    public ArrayList<NearbyUserModel> getUsers() {
        return users;
    }

    public void setUsers(ArrayList<NearbyUserModel> users) {
        this.users = users;
    }
}
