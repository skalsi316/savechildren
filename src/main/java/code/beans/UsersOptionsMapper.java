package code.beans;


public interface UsersOptionsMapper {

	UsersOptions selectByUserID(String userid);	

	void insertUsersOptionsData(UsersOptions userOptions);

}
