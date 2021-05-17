# customerstatement

SpringBoot application - Rabobank Customer Statements

## Start Application
1. mvn spring-boot:run

## Implementation
A) Back-end controller calls :

1. Save Customer Statements :
	URL : /customerstatement
	DESCRIPTION : Save customer statements with correct information
		```{
			"transactionReference": xxxxx,
			"accountNumber": "xxxxxxx",
			"startBalance": xx.xx,
			"mutationType": "-xxxx",
			"description": "xxxxxxx",
			"endBalance": xx.xx
		}```
	
2. Get Customer Statements :
	URL : /customerstatement
	DESCRIPTION : Able to get customer statements which are saved.
   

Eg : [GET/POST] http://localhost:8080/customerstatement