package application;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import accounts.Account;
import accounts.AdminAccount;
import accounts.UserAccount;
import encryptors.CaesarEncryptor;
import encryptors.ModCaesarEncryptor;

public class Util {
	
	// Methods
	@SuppressWarnings("unchecked")
	public static void writeAccountsOnJSON(ArrayList<Account> list, 
			AppData data) {
		CaesarEncryptor ce = new ModCaesarEncryptor();
		JSONArray JSONList = new JSONArray();
		
		for(Account item : list) {
			String[] crypted = ce.encrypts(item.getPhoneNumber(), 
					item.getAccountEmail(), item.getAccountPw());
			
			JSONObject obj1 = new JSONObject();
			obj1.put("account_ID", item.getID());
			obj1.put("account_type", item.getAccountType().toString());
			obj1.put("name", item.getName());
			obj1.put("surname", item.getSurname());
			obj1.put("phone_number", crypted[0]);
			obj1.put("account_email", crypted[1]);
			obj1.put("account_pw", crypted[2]);
	        
	        JSONObject obj2 = new JSONObject(); 
	        obj2.put("account", obj1);
	        JSONList.add(obj2);
		}
		Util.writeOnJSON(data.getRootPath() + "/accounts_data.json", JSONList);
	}
	
	public static void readAccountsFromJSON(String rootPath,
			ArrayList<Account> list) {
		JSONParser parser = new JSONParser();
		
		try {
			Object obj = parser.parse(new FileReader(rootPath 
					+ "/accounts_data.json"));
            JSONArray usersDataFile = (JSONArray) obj;
            for(Object us: usersDataFile) {
            	Util.addAccountToList((JSONObject) us, list);
            }
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch(ParseException e) {
			e.printStackTrace();
		}
	}
	
	public static void addAccountToList(JSONObject us, 
			ArrayList<Account> list) {
		JSONObject obj = (JSONObject) us.get("account");
		CaesarEncryptor ce = new ModCaesarEncryptor();
		String[] decrypted = ce.decrypts((String) obj.get("phone_number"), 
				(String) obj.get("account_email"), 
				(String) obj.get("account_pw"));
		
		@SuppressWarnings("unused")
		String ID = (String) obj.get("account_ID");
		String accountType = (String) obj.get("account_type");
		String name = (String) obj.get("name");
		String surname = (String) obj.get("surname");
		String phoneNumber = decrypted[0];
		String accountEmail = decrypted[1];
		String accountPw = decrypted[2];
		Account account = null;
		switch(accountType) {
		case "ADMINISTRATOR":
			account = new AdminAccount(name, surname, phoneNumber, 
					accountEmail, accountPw);
			break;
		case "USER":
			account = new UserAccount(name, surname, phoneNumber, 
					accountEmail, accountPw);
			break;
		case "PREMIUM_USER":
			// TODO
			break;
		}
		list.add(account);
	}

	public static void writePasswordsOnJSON() {
		// TODO
	}
	
	public static void readPasswordsOnJSON() {
		// TODO
	}
	
	public static void writeOnJSON(String filePath, JSONArray array) {
		try(FileWriter appDataFile = new FileWriter(filePath)){
            appDataFile.write(array.toJSONString()); 
            appDataFile.flush();
            appDataFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
	
}