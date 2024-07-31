/********************************************************************************************************2*4*w*
 * File:  SecurityUserSerializer.java
 * Course materials CST 8277
 *
 * @author Teddy Yap
 * @author Shariar (Shawn) Emami
 * @author Mike Norman
 *   
 * Updated by:  Group 16
 *   041068533, Lei, Luo 
 *   041062508, Yang, Mi 
 *   041066092, Yueying, Li 
 *   041079885, Miao, Yang  
 *   Date modified: 2024-07-24
 */
package acmecollege.rest.serializer;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import acmecollege.entity.SecurityUser;
 
public class SecurityUserSerializer extends StdSerializer<Set<SecurityUser>> implements Serializable {
    private static final long serialVersionUID = 1L;

    public SecurityUserSerializer() {
        this(null);
    }

    public SecurityUserSerializer(Class<Set<SecurityUser>> t) {
        super(t);
    }

    /**
     * This is to prevent back and forth serialization between many-to-many relations.<br>
     * This is done by setting the relation to null.
     */
    @Override
    public void serialize(Set<SecurityUser> originalUsers, JsonGenerator generator, SerializerProvider provider)
        throws IOException {
        
        Set<SecurityUser> hollowUsers = new HashSet<>();
        for (SecurityUser originalUser : originalUsers) {
            // Create a 'hollow' copy of the original Security Users entity
            SecurityUser hollowP = new SecurityUser();
            hollowP.setPwHash(originalUser.getPwHash());
            hollowP.setId(originalUser.getId());
            hollowP.setUsername(originalUser.getUsername());
            hollowP.setStudent(originalUser.getStudent());
            hollowP.setRoles(null);
            hollowUsers.add(hollowP);
        }
        generator.writeObject(hollowUsers);
    }
}