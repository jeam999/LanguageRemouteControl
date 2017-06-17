package alexeykafeev.languageremoutecontrol;

import java.net.InetAddress;

/**
 * Created by jeam999 on 09.06.2017.
 */

public class Student_Item {

    InetAddress addres;
    String firstName;
    String lastName;

    public Student_Item(InetAddress address,String firstName,String lastName) {
        this.addres=address;
        this.firstName=firstName;
        this.lastName=lastName;
    }

    public InetAddress getAddres() {
        return addres;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setAddres(InetAddress addres) {
        this.addres = addres;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

}
