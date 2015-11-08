package vault;

public class Long_texts 
{

	public static final String about_passwords = ""
			+"You must use good passwords\n"
			+"Without a good password, using encryption would be...\n"
			+"as ridiculous as wearing a body armor made from cigarette paper.\n"
			+"The reason is: there are attack techniques that will try to break the vault\n"
			+"(that is: break the cryptographic algorithm)\n"
			+"but lazy guys will simply try passwords...\n"
			+"And the problem today is that processors are very fast...\n"
			+"So it is possible to try thousands of passwords per second, or more... !!!\n\n"
			+"What is a good password?\n"
			+"  1. It must contain a mix of uppercases, lowercases and numbers\n"
			+"   ...adding charecters such as ~!@#()-+=$%^&* etc... can also help a lot\n"
			+"  2. It must not be made of words that can be found in a dictionary\n"
			+"  3. It must be as long as possible, optimaly the max is 32 characters for Vault\n"
			+"   Vault will let you type passwords longer than 32 characters, but the trailing ones are discarded\n\n"
			+"   PASSWORD TIPS\n"
			+"   1. I use songs & poems for remembering long passwords, for example:\n"
			+"   ->voui^all^live^in^a^yelow^Zubmarine<-\n"
			+"   2. It is 34 character long (rule#3)\n"
			+"   3. See how I replaced spaces with a special character? (rule#1)\n"
			+"   4. See how I distorted words? (rule #2)\n"
			+"         we=>voui,\n"
			+"         spelling error on yellow\n"
			+"         capital Z for submarine (both rule #1 & #2)\n"
			+"   5. DONT write passwords in an electronic document unless you then encrypt that document\n"
			+"   6. Avoid sending passwords in non-encrypted email, prefer a different chanel like SMS, or a voice call, etc\n"
			+"   7. Post-its are OK if you think your attacker is a hacker and that hackers are not burglars\n";			

	public static final String about_vault = ""
			+"                        Vault 1.0\n"
			+"             Copyright Philippe Gentric 2015\n"
			+"Vault is a 100% java app\n"
			+"Vault is based on org.bouncycastle.crypto version 1.52\n"
			+"The code is available at github\n"
			+"It uses AES with a different random Initialization Vector for each operation\n"
			+"It can uses keys of up to 256 bits... (directly from password with replication*)\n"
			+"If you dont know what that means, the translation is:\n"
			+"Short answer: Vault is pretty safe.\n"
			+"Long answer: Vault is safe because:\n"
			+"1. It is based on a known cryptographic engine\n"
			+"2. The source code is available to experts for review, which means:\n"
			+"2.a Experts can check the code is correct in terms of security principles\n"
			+"2.b Experts especially can check the code does not have any backdoor\n"
			+"3. It uses the state-of-the-art cryptographic algorithm\n\n"
			+"*replication: if you provide a password shorter than 32 characters, it is extended by copying until the 32 character limit is reached\n";

	public static final String vault_files_help =""
			+"Vault_files can encrypt files.\n"
			+"Vault_files is intended to ensure confidentiality of data on unsafe medium.\n"
			+"Example usage scenario:\n"
			+ "   You want to give to a partner a marketing plan on a USB key.\n"
			+"    You zip the folder using 7zip.\n"
			+"    You encrypt the zip file using Vault_files.\n"
			+"    You send the password to your partner in a SMS.\n"
			+"    Your partner copies the encrypter zip file from the USB key to his laptop\n"
			+"    Your partner decrypts the zip file using the password from the SMS\n\n"
			+"An encryption operation will create a file with the extension "+Pg_crypt.extension+"\n"
			+"A decryption operation will restore the original content (file or whole folder).\n\n"
			+"You just need to grab the icon of your content (a file, a directory/folder)...\n"
			+"drag it...\n"
			+"drop it in the landing zone...\n"
			+"type your password when asked... that's it!\n\n"
			+"How to change password for already encrypted document?\n"
			+"	Decrypt and re-encrypt it: Vault_files will prompt you for the new password\n";

	public static final String vault_secrets_help =""
			+"Vault_files is intended to keep safe your secrets such a passwords, credit card codes etc.\n"
			+"Vault_secrets keeps secrets in an encrypted file.\n"
			+"Vault_secret decrypts that file exclusively in RAM:\n"
			+"that way, your secrets are never exposed in the file system\n\n"
			+"Vault_secret ask your password once per session \n"
			+"so you have to type your password only once when you start Vault_secrets\n"
			+"Vault_secrets DOES NOT store the password on the disk but keeps it in RAM\n"
			+"How to change password for Vault_secrets?\n"
			+"	  You will need to use Vault_files to decrypt the secret file,\n"
			+"	  and then re-encrypt it with the new password\n";
	
	public static final String why_vault_is_safe = ""
			+"If you have data sensitive enough to be elected for encryption, you do not want to loose it\n"
			+"Vault was DESIGNED with this concern in mind:\n"
			+"For that reason Vault NEVER deletes one of your file, except when asked.\n"
			+"This means that the clear text version remains after encryption\n"
			+"This means that the encrypted version remains after decryption\n"
			+"This means that if a file already exists:\n"
			+"    - either the produced file will be SAFELY renamed (xxx_1)\n"
			+"    -or Vault will abort\n\n"
			+"Vault checks that DECRYPTION completed correctly, using the signature of the original file.\n\n"
			+"Vault checks that all file operations complete correctly and will tell you if a problem occured.\n"
			+"In short, Vault CANNOT corrupt one of your files.\n\n"
			+".... one caveat of this... is that some cleanup has to be done...manually or at the end of a session\n\n";

	public static final String dont_forget_password = ""
			+"Sorry but there is NO WAY to recover data if the password is forgotten.\n"
			+"Think! If there would be a way, that would be unsafe!\n\n";
	/*
			+"In this respect, passwords for encryption are fundamentally different than passwords for acounts.\n"
			+"Account passwords protect the connection to something: time is involved.\n"
			+"Resetting an account password allows to open the connection for a very short period of time.\n"
			+"Encryption passwords protect data. Time is not involved. Either you can decrypt, or you cannot.\n"
			+"If you can decrypt something, you can have it forever by making a copy.\n";

	public static final String how_often_change_password = ""
			+"   How often should I change password(s)?\n"				
			+"   ... it is of relatively little use to change password often when encrypting files\n"
			+"   ... why? because if someone stole a copy of an encrypted file, you cannot change the password of that copy...\n"
			+"   ... the attacker can take his (her) whole life time to try-and-find the password!\n"
			+"   ... but if you have only one password and one of your encrypted file was stolen...\n"
			+"   then of course CHANGE the password for all files encrypted using the same password\n"
			+"   exactly like you change locks when you loose a copy of your keys,\n"
			+"   since you CANNOT know if the guy who found them knows where you live.\n"
			+"   However the risk you have to consider is when someone stole en encrypted file without you being aware...\n\n"
			+"   It is different for ACCOUNT PASSWORDS: you should change them as often as tolerable.\n"
			+"   Why? I someone can access your account, he can steal your encrypted file(s) and take all the time and use all possible means to break in\n"
			+"   So file encryption is a gadget if you dont have good account passwords protecting your computer, and your key onlime accounts (mail, cloud storage, etc)\n";

	public static final String encrypting_twice = ""
			+"One way to get a better security is to encrypt several times\n"
			+"Why?\n"
			+"When an attaker tries passwords, how does he (she) know that the password has been found?.\n"
			+"One good clue is when the decrypted file makes sense* (it is a text, or an image, or a valid file archive).\n"
			+"So if after finding the password the file produced is garbage, the attacker has to assume another decryption is required...\n"
			+"And of course, he (she) will try the same password!\n"
			+"Conclusion: multiple encryption REQUIRES multiple passwords...\n\n"
			+"Vault CAN do multiple encryption, but you will have to do it MANUALLY\n"
			+"What? Normally, if you pass an encrypted file to Vault, Vault will decrypt it!\n"
			+"The ONLY reason is: the file name has the extension: "+Pg_crypt.extension+" \n"
			+"So if you REMOVE that extension, Vault will encrypt the file a second time \n"
			+"You can do that as many times as you want... \n"
			+"But remember: you should use a different password for each stage... \n"
			+"So in the end, that's a lot of password to remember... \n"
			+"\n"
			+"*Vault encrypted files contain a crypto graphic signature (MD5 hash) of the original file\n"
			+"Vault uses this to verify that the decryption went OK\n"
			+"One way decryption can go wrong is if you provided a wrong password\n"
			+"But most of the time a wrong password will cause a cryptographic error before the end of the decryption\n";
	*/
	public static final String explain_safe_files = ""
			+"Vault is 100% paranoid about loosing a secret accidentally\n"
			+"For this reason Vault will create a ** BACKUP COPY ** of ** ENCRYPTED ** files, ** EVERYTIME ** there is a change.\n"
			+"So that even if Vault crashes or is killed, your \"mistakes\" CAN be recovered, but remain SAFE (encrypted)\n"

			+"\n\n Cleanup.\nIt also means possibly quite many such files on your disk...\n"
			+"For this reason Vault will ASK you when you exit the application\n"
			+" if you want to clean up, that is, remove all these temporary backups\n"
			+" if you dont click OK, the files are not deleted\n"
			+" if you want to cleanup, just click OK: if nothing wrong happenned, this is safe\n"

			+"\n\n How to use a backup file to recover an accidentally lost item\n"
			+"To recover the data in a backup file the recipe is as follows:\n"
			+" - find the right file by looking at the modification date\n"
			+" - DRAG the file into Vault_files main drop zone\n"
			+" - type the password: BEWARE: this creates a clear-text file !!!!!!\n"
			+" - recover what you need from the clear-text file\n"
			+" - DELETE the unsafe clear-text file** using the cleanup menu in Vault_files\n"
			+" \n\n**remember that OS-deleting a file DOES NOT erase the data: use Vault_files sure-delete or equivalent\n\n";

	public static final String explain_unsafe_files = ""
			+"Vault is 100% paranoid about loosing a secret accidentally\n"
			+"For this reason Vault keeps track of:\n"
			+" (1) generated CLEAR TEXT files (resulting from a decryption operation)\n"
			+" (2) acquired CLEAR TEXT files (discovered during an encryption operation)\n"
			+" before exiting Vault will ask you if you want to sure-delete these files\n";

	public static final String explain_sure_deleting = ""
			+"Remember that OS-deleting a file DOES NOT erase the data\n"
			+"In fact, there are un-delete programs that can recover the content of \"deleted\" files!\n"
			+"Now the question is: how can I erase a file FOR GOOD?\n"
			+"Short answer: Vault_files can do it for you. It is called sure-deleting.\n"
			+"Long answer: Files are really deleted on disk when they are replaced by something else.\n"
			+"1) One way is to create a full disk event after the target file deletion.\n"
			+"   For example take a large file (say a movie) and create as many copies as it takes to fill the disk\n"
			+"   At some point the OS will reuse the physical space left by deleting your target file\n"
			+"   ...but you have no control over when this occurs... so this will take as long as it takes to fill your disk\n"
			+"2) Another way is to REPLACE the file content by something else BEFORE OS-deleting the file.\n"
			+"   and the good news is: Vault_files can do this for you!\n"
			+"   When sure-deleting, Vault_files first replaces the inside of the file with garbage before OS-deleting it.\n"
			+"   To check how this works, you can ask Vault_files to just replaces the inside of the file with garbage\n";

	public static final String known_issues = "None";
			/*+"- Vault can only handle SIMPLE directory structures\n"
			+"      simple means: a folder that contrains only files, not additional folders"
			+"	    WORKAROUND: use your favorite archiver (tar,zip,7zip etc) and then use Vault to encrypt the result\n"
			+"- If you remove the .tar in the name of an encrypted TAR file, Vault will fail to detar\n"
			+"	    WORKAROUND: add .tar to the name of the produced file and use your favorite dezipper to detar it\n"
			+"- If you rename an encrypted TAR file, Vault will fail to detar\n"
			+"	    WORKAROUND: you must put back the original name\n";
			*/
			
	protected static final String what_is_unsafe = ""
			+"If you leave clear text aka decrypted files on the drive, it is possibly unsafe,\nfor example in case your computer is stolen or hacked\n"
			+"When you perform file encryption to protect a secret,\nthe source clear text should be sure-deleted after successful encryption.\n"
			+"When you perform file decryption to look at a secret,\nthe temporary clear text should be sure-deleted as soon as you dont need it anymore.";

	protected static final String what_is_safe = ""
			+"If you leave encrypted backup files on the drive, it safe... but wastes disk space\n"
			+"When you perform file encryption to protect a secret, the original encrypted file is renamed to enable error recovery.\n"
			+"When you are finished doing modifications and you are sure there were no errors, safe backup encrypted files should be deleted.";

	protected static final String explain_peek = ""
			+"Having a peek at an encrypted file consist in decrypting it in RAM\n"
			+"to have a look at it, or change it\n";






}
