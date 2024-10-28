package com.bharath.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.regex.Pattern;

import com.bharath.MainCentralizedResource;
import com.bharath.dao.UsersDao;
import com.bharath.dao.UsersDaoImpl;
import com.bharath.model.Users;


public class UsersServiceImpl implements UsersService{
	private static UsersServiceImpl usersServiceImpl = null;
	private UsersDao usersDaoImpl = null;

	private UsersServiceImpl() {
		usersDaoImpl = UsersDaoImpl.getInstance();
	}
	
	/**
	 * This will provide the instance of UsersServiceImpl class to achieve Singletone
	 * @return
	 */
	public static UsersServiceImpl getInstance(){
		if(usersServiceImpl == null){
			usersServiceImpl = new UsersServiceImpl();
		}
		return usersServiceImpl;
	}
	
	/**
	 * This method is used to validate the user id
	 */
	@Override
	public boolean isValidUserId(int userId){
		if(userId <= 0){
			MainCentralizedResource.LOGGER.warning("User id incorrect, userid: " + userId);
			return false;
		}
		
		
		boolean isExits = usersDaoImpl.isUserIdExits(userId);
		if(!isExits){
			MainCentralizedResource.LOGGER.warning("User id incorrect, userid: " + userId);
		}
		return isExits;
	}
	
	/**
	 * This method validate the credentials entered by the user
	 */
	@Override
	public boolean isCorrectCredentials(int userId, String password){
		
		 Users users = usersDaoImpl.getUser(userId);
	     String hashedPassword = MainCentralizedResource.generateHashedPassword(password);
	     
	     boolean isPasswordCorrect = users.getPassword_().equals(hashedPassword);
	     if(!isPasswordCorrect){
	    	 MainCentralizedResource.LOGGER.warning("Password incorrect!..");
	     }
	     
	     return isPasswordCorrect;
	}
	
	/**
	 * This method is used to check whether the user account is blocked 
	 */
	@Override
	public boolean isUserBlocked(int userId){
		Users users = usersDaoImpl.getUser(userId);
		boolean isUserBlocked = users.isBlocked() == 'y';
		
		if(isUserBlocked){
			MainCentralizedResource.LOGGER.warning("Your account is blocked cannot login:: user id - " + userId);
		}
		
		return isUserBlocked;
	}
	
	/**
	 * This method will return the user object for the specified user_id
	 */
	@Override
	public Users getUser(int userId){
		Users users = usersDaoImpl.getUser(userId);
		if(users == null){
			MainCentralizedResource.LOGGER.warning("Something went wrong in fetching the user with userid: "+userId);
		}
		return users;
	}
	
	/**
	 * This method is used to get all users in the social media application
	 */
	@Override
	public List<Users> getAllUsers(){
		List<Users> listOfAllUsers = usersDaoImpl.listAllUsers();
		if(listOfAllUsers == null){
            MainCentralizedResource.LOGGER.warning("Something went wrong in fetching all Users list");
		}
		return listOfAllUsers;
	}

	/**
	 * This method is used to validate the user name while signing up 
	 */
	@Override
	public boolean isValidUserName(String userName){
		if(userName == null || userName.isEmpty() || userName.matches("\\d+")){
			MainCentralizedResource.LOGGER.warning("Entered User name "+userName+" is invalid!");
			return false;
		}
		MainCentralizedResource.LOGGER.info(userName+ " passed the username validation");
		return true;
	}

	/**
	 * This method is used to check whether the password entered is meeting the criteria
	 */
	@Override
	public boolean isValidPassword(String password) {
		
		if(password== null || password.isEmpty() || (!Pattern.matches( "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,20}$", password))){
			MainCentralizedResource.LOGGER.warning("Password : \""+password+"\" entered is invalid");
			return false;
		}
		MainCentralizedResource.LOGGER.info(password+ " passed the password validation");
		return true;
	}

	/**
	 * This method is used to check the dob entered is meeting the criteria
	 */
	@Override
	public boolean isDobValid(String dob) {
		
		if(dob == null || dob.isEmpty()){
			MainCentralizedResource.LOGGER.warning("Entered dob \""+dob+"\" is invalid");
			return false;
		}
		
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		LocalDate birthDayDate = LocalDate.parse(dob, dateTimeFormatter);
		if(birthDayDate.isBefore(LocalDate.now().minusYears(125)) || birthDayDate.isAfter(LocalDate.now().minusYears(18))){
			MainCentralizedResource.LOGGER.warning("Entered dob \""+dob+"\" is invalid");
			return false;
		}
		
		MainCentralizedResource.LOGGER.info(dob+" passed the dob validation");
		return true;
	}

	/**
	 * This method is used to check whether the email id meets the criteria
	 */
	@Override
	public boolean isEmailIdValid(String emailId) {
		if(emailId == null || emailId.isEmpty() || (!Pattern.matches("^[a-zA-Z0-9+_.-]+@[a-zA-Z0-9.-]+$", emailId))){
			MainCentralizedResource.LOGGER.warning("Entered emailId \""+emailId+"\" is invalid");
			return false;
		}
		MainCentralizedResource.LOGGER.info(emailId+" passed the emailId validation");
		return true;
	}

	/**
	 * This method is used to check whether the email id is already exists in the database
	 */
	@Override
	public boolean isEmailIdExists(String emailId) {
		UsersDaoImpl usersDaoImpl = UsersDaoImpl.getInstance();
		boolean isEmailExists = usersDaoImpl.isEmailAlreadyExits(emailId);
		
		if(isEmailExists){
			MainCentralizedResource.LOGGER.warning(emailId + " email id already exists");
		}else{
			MainCentralizedResource.LOGGER.warning(emailId + " email id is not exists");
		}
		
		return isEmailExists;
	}

	/**
	 * This method is used to add new user
	 */
	@Override
	public int addNewUser(Users users) {
		UsersDaoImpl usersDaoImpl = UsersDaoImpl.getInstance();
		int userid = usersDaoImpl.addNewUser(users);
		
		if(userid == -1){
			MainCentralizedResource.LOGGER.warning("Something went wrong in creating the user");
		}else{
			MainCentralizedResource.LOGGER.warning("User with userid: "+userid+" crated successfully");
		}
		return userid;
	}

	/**
	 * This method is used to get the user id by email id
	 */
	@Override
	public int getUserIdByEmailId(String emailId) {
		UsersDao usersDaoImpl = UsersDaoImpl.getInstance();
        int userId = usersDaoImpl.getUserIdByEmailId(emailId);
        if(userId == -1){
        	MainCentralizedResource.LOGGER.warning("Enterd email id: \""+emailId+"\" is invalid");
        }else{
        	MainCentralizedResource.LOGGER.info(emailId+" email id is valid");
        }
		return userId;
	}

	/**
	 * This method validates the email and dob entered by the user while they are forget password
	 */
	@Override
	public boolean isValidEmailAndDobOfUser(String emailId, String dob) {
		int userId = this.getUserIdByEmailId(emailId);
		
		if(userId == -1){
			return false;
		}
		
		if(!this.isDobValid(dob)){
			return false;
		}
		
		Users users = this.getUser(userId);
		
		if(! LocalDate.parse(users.getBirthday_(), DateTimeFormatter.ofPattern("yyyy-MM-dd")).equals(LocalDate.parse(dob, DateTimeFormatter.ofPattern("yyyy-MM-dd")))){
			MainCentralizedResource.LOGGER.warning("Entered dob "+dob+" is not matching with user");
			return false;
		}
		
		return true;
	}

	/**
	 * This method checks whether the new password and confirm password is equal while we are changing the password
	 */
	@Override
	public boolean isNewAndConfirmPasswordMatching(String newPassword, String confirmPassword) {
		if(newPassword == null || confirmPassword == null || (!newPassword.equals(confirmPassword))){
			MainCentralizedResource.LOGGER.warning("New Password and Confirm password are mismatching");
			return false;
		}
		
		MainCentralizedResource.LOGGER.info("New Password and Confirm password are matching");
		return true;
	}
	
	/**
	 * This method is used to check whether the new password is same as old password
	 */
	@Override
	public boolean isOldPasswordAndNewPasswordAreSame(Users users, String newPassword) {
		String oldHashedPassword = users.getPassword_();
		String newHashedPassword = MainCentralizedResource.generateHashedPassword(newPassword);
		if(oldHashedPassword.equals(newHashedPassword)){
			MainCentralizedResource.LOGGER.warning("Old password and new Password are same");
			return true;
		}
		
		MainCentralizedResource.LOGGER.info("Old password and new Password are different");
		return false;
	}
	
	/**
	 * This method is used to change the user password
	 */
	@Override
	public boolean resetOwnPassword(int userId, String password) {
		String hashedPassword = MainCentralizedResource.generateHashedPassword(password);
		boolean isPasswordResetSuccessfull = usersDaoImpl.resetOwnPassword(userId, hashedPassword);
		
		if(isPasswordResetSuccessfull){
			MainCentralizedResource.LOGGER.info("Successfully reset the password for user id: "+ userId);
		}else{
			MainCentralizedResource.LOGGER.info("Reset the password for user id: "+ userId+" was failed");
		}
		
		return isPasswordResetSuccessfull;
	}

	/**
	 * This method is used to check the specified user is admin
	 */
	@Override
	public boolean isAdminCheck(int userId) {
		
		boolean isValidUser = this.isValidUserId(userId);
		
		if(!isValidUser){
			MainCentralizedResource.LOGGER.warning("Your user id is invalid!");
			return false;
		}
		
		boolean isAdmin = usersDaoImpl.isAdminCheck(userId);
		if(isAdmin){
			MainCentralizedResource.LOGGER.info("User with user id "+ userId +" is an admin user");
		}else{
			MainCentralizedResource.LOGGER.info("User with user id "+ userId +" is not an admin user");
		}
		return isAdmin;
	}

	/**
	 * This method is used to update the role of the user
	 */
	@Override
	public boolean updateTheRoleOfTheUser(int userId, int targetUserId, String role) {
		if(userId == targetUserId){
			MainCentralizedResource.LOGGER.warning("One cannot change his own role");
			return false;
		}
		
		if(!(role.equals("admin") || role.equals("user"))){
			MainCentralizedResource.LOGGER.warning("Role "+role+" is invalid!");
			return false;
		}
		
		boolean isUserIdAdmin = this.isAdminCheck(userId);
		if(!isUserIdAdmin){
			MainCentralizedResource.LOGGER.warning("User "+userId+" has no privilage to change the role of other user");
			return false;
		}
		
		boolean isTargetUserAdmin = this.isAdminCheck(targetUserId);
		if(role.equals("admin") && isTargetUserAdmin){
			MainCentralizedResource.LOGGER.warning("Target User "+targetUserId+" is already an admin user");
			return false;
		}
		
		if(role.equals("user") && (!isTargetUserAdmin)){
			MainCentralizedResource.LOGGER.warning("Target User "+targetUserId+" is already a normal user");
			return false;
		}
		
		if(role.equals("admin")){
			boolean isRoleChangeToAdmin = usersDaoImpl.updateTheRoleOfTheUserToAdmin(targetUserId);
			if(isRoleChangeToAdmin){
				MainCentralizedResource.LOGGER.info("User "+userId+" have updated the role of "+targetUserId+" user to admin");
			}else{
				MainCentralizedResource.LOGGER.info("User "+userId+" try to update the role of "+targetUserId+" user from normal user to admin was failed");
			}
			return isRoleChangeToAdmin;
		}else{
			boolean isRoleChangeToUser = usersDaoImpl.updateTheRoleOfTheAdminToUser(targetUserId);
			if(isRoleChangeToUser){
				MainCentralizedResource.LOGGER.info("User "+userId+" have updated the role of "+targetUserId+" admin to user");
			}else{
				MainCentralizedResource.LOGGER.info("User "+userId+" try to update the role of "+targetUserId+" admin to normal user was failed");
			}
			return isRoleChangeToUser;
		}
	}


	/**
	 * This is used to remove the user account
	 */
	@Override
	public boolean removeUser(int userId, int targetUserId) {
		if(userId== targetUserId){
			MainCentralizedResource.LOGGER.warning("One cannot remove his own account");
			return false;
		}
		
		boolean isUserIdAdmin = this.isAdminCheck(userId);
		if(!isUserIdAdmin){
			MainCentralizedResource.LOGGER.warning("User "+userId+" has no privilage to change the role of other user");
			return false;
		}
		
		boolean isTargetUserIdValid = this.isValidUserId(targetUserId);
		if(!isTargetUserIdValid){
			MainCentralizedResource.LOGGER.warning("Target user id "+targetUserId+" is not valid");
			return false;
		}
		
		boolean isAccountRemovedSuccessfully = usersDaoImpl.removeUser(targetUserId);
		
		if(isAccountRemovedSuccessfully){
			MainCentralizedResource.LOGGER.info("User "+userId+" removed the "+targetUserId+" user account");
		}else{
			MainCentralizedResource.LOGGER.warning("Removing "+targetUserId+" user account by "+userId+" user was failed");
		}
		
		return isAccountRemovedSuccessfully;
	}

	/**
	 * This method is used to unblock the user
	 */
	@Override
	public boolean unBlockUser(int userId, int targetUserId) {
		if(userId == targetUserId){
			MainCentralizedResource.LOGGER.warning("User cannot unblock his own account");
			return false;
		}
		
		boolean isUserIdAdmin = this.isAdminCheck(userId);
		
		if(!isUserIdAdmin){
			MainCentralizedResource.LOGGER.warning("User "+userId+" has no privilage to unblock other user");
			return false;
		}
		
		boolean isTargetUserIdValid = this.isValidUserId(targetUserId);
		if(!isTargetUserIdValid){
			MainCentralizedResource.LOGGER.warning("Target user id "+targetUserId+" is not valid");
			return false;
		}
		
		Users users = this.getUser(targetUserId);
		boolean isTargetUserBlocked = users.isBlocked() == 'y';
		
		if(!isTargetUserBlocked){
			MainCentralizedResource.LOGGER.warning("User id "+targetUserId+" is already unblocked");
			return false;
		}
		
		boolean isUnBlockingTheUserSuccessfull = usersDaoImpl.unBlockUser(targetUserId);
		
		if(isUnBlockingTheUserSuccessfull){
			MainCentralizedResource.LOGGER.info("User "+userId+" unblocking the user "+targetUserId+" was successfull");
		}else{
			MainCentralizedResource.LOGGER.info("User "+userId+" unblocking the user "+targetUserId+" was failed");
		}
		
		return isUnBlockingTheUserSuccessfull;
	}

	/**
	 * This method is used to block the specified user
	 */
	@Override
	public boolean blockUser(int userId, int targetUserId) {
		if(userId == targetUserId){
			MainCentralizedResource.LOGGER.warning("User cannot block his own account");
			return false;
		}
		
		boolean isUserIdAdmin = this.isAdminCheck(userId);
		
		if(!isUserIdAdmin){
			MainCentralizedResource.LOGGER.warning("User "+userId+" has no privilage to block other user");
			return false;
		}
		
		boolean isTargetUserIdValid = this.isValidUserId(targetUserId);
		if(!isTargetUserIdValid){
			MainCentralizedResource.LOGGER.warning("Target user id "+targetUserId+" is not valid");
			return false;
		}
		
		Users users = this.getUser(targetUserId);
		boolean isTargetUserBlocked = users.isBlocked() == 'y';
		
		if(isTargetUserBlocked){
			MainCentralizedResource.LOGGER.warning("User id "+targetUserId+" is already blocked");
			return false;
		}
		
		boolean isUserBlocked = usersDaoImpl.blockUser(targetUserId);
		if(isUserBlocked){
			MainCentralizedResource.LOGGER.info("User "+userId+" blocking the user "+targetUserId+" was successfull");
		}else{
			MainCentralizedResource.LOGGER.info("User "+userId+" blocking the user "+targetUserId+" was failed");
		}
		return isUserBlocked;
	}
	
}
