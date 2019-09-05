<?php

class DBOperations{

	 private $host = '127.0.0.1';
	 private $user = 'root';
	 private $db = 'smp_yppui';
	 private $pass = '';
	 private $conn;

public function __construct() {

	$this -> conn = new PDO("mysql:host=".$this -> host.";dbname=".$this -> db, $this -> user, $this -> pass);

}


 public function insertData($name,$email,$password){

	$options = [
		'cost' => 12,
	];
				
 	$encrypted_password = password_hash($password, PASSWORD_BCRYPT, $options);

 	$sql = 'INSERT INTO users_daftar SET nama =:name,
    email =:email,password =:encrypted_password';

 	$query = $this ->conn ->prepare($sql);
 	$query->execute(array(':name' => $name, ':email' => $email, ':encrypted_password' => $encrypted_password));
	
	$sql = 'SELECT * FROM users_daftar WHERE email = :email';
    $query = $this -> conn -> prepare($sql);
    $query -> execute(array(':email' => $email));
    $data = $query -> fetchObject();

    if ($query) {
        $user["name"] = $data -> nama;
        $user["email"] = $data -> email;
        return $user;

    } else {
        return false;

    }
 }


 public function checkLogin($email, $password) {

    $sql = 'SELECT * FROM users_daftar WHERE email = :email';
    $query = $this -> conn -> prepare($sql);
    $query -> execute(array(':email' => $email));
    $data = $query -> fetchObject();
    $encrypted_password = $data -> password;

    if (password_verify($password, $encrypted_password)) {

        $user["name"] = $data -> nama;
        $user["email"] = $data -> email;
        return $user;

    } else {
        return false;
    }

 }


 public function changePassword($email, $password){

	$options = [
		'cost' => 12,
	];
				
    $encrypted_password = password_hash($password, PASSWORD_BCRYPT, $options);

    $sql = 'UPDATE users_daftar SET password = :encrypted_password WHERE email = :email';
    $query = $this -> conn -> prepare($sql);
    $query -> execute(array(':email' => $email, ':encrypted_password' => $encrypted_password));

    if ($query) {
        return true;

    } else {
        return false;

    }

 }

 public function checkUserExist($email){

    $sql = 'SELECT COUNT(*) from users_daftar WHERE email =:email';
    $query = $this -> conn -> prepare($sql);
    $query -> execute(array('email' => $email));

    if($query){
        $row_count = $query -> fetchColumn();

        if ($row_count == 0){
            return false;

        }
		else {
            return true;
        }
    }
	else {
        return false;
    }
 }
}




