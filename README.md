# fiscalservicesdk

This repository provides a guidance on integrating an Android app with a myPOS N5Fiscal device. Once integrated, the app will be able to communicate with the fiscal core device components in order to manage all fiscal operations required by the bulgarian fiscal law - registering sales in the fiscal memory of the device, fiscal tickets and inovices printing, fiscal reports printing etc.).
The built-in functionalities of fiscalservicesdk allows you to send requsts to N5Fiscal device in order to perform fiscal operations, using a specialy developed N5Fiscal Communication Protocol. It is a high level protocol, based on the JSON-RPC 2.0 standard.
Since The N5Fiscal device provides fiscal and EFT functions as well, the Communication Protocol provides also card payments processing (including but not limited to VISA and Mastercard).
Please follow the attached description of the protocol to understand the fiscal and payment operations essence.


### Table of Contents
* [Installation](#installation)
* [Usage](#Usage)
	* [Fiscal Controller Utilization](#fiscal-controller-utilization)
	* [Perform a Fiscal Command Requests](#perform-fiscal-command-request)
	* [Fiscal Command Request Example](#fiscal-command-request-example)


## [Installation]

1. build the project fiscalservicesdk or use the fiscalservicesdk.aar package from the repository
2. include fiscalservicesdk.aar in your project.

## [Usage]


### Fiscal Controller Utilization

Ones per app lifecicle the bind method must be invoked to utilize the fiscal controller connection.

```java
FiscalService.getInstance().bind(.....);
```

### Perform Fiscal Command Request

Use exchangeCommand method to perform a Fiscal Command Request.
Fiscal commands must be generated by the app and must comply with JSON-RPC 2.0 standard (Request Object and Response Object - see the examples bellow). 
The Fiscal device provides a Fisacl Command Request validation. The incorrect requests are rejected.
Please follow the N5Fiscal Communication Protocol description to get more information abaut Request Object and Response Object structure reqirements.


```java

String response =  FiscalService.getInstance().exchangeCommand(jsonCommand, timeout);

//jsonCommand is a json formatted String, containing a valid Fiscal Command request, according to the N5Fiscal Communication Protocol description.
//timeout is defined in miliseconds.
//response is a json formatted String, containing a the Fiscal Command execution response, according to the N5Fiscal Communication Protocol description.
```

### Fiscal Command Request Example

The following Fiscal Command Request Object shows how to request sale registration and fiscal ticket printing on myPOS N5Fiscal device:


```json
{
	"id": 1,
	"jsonrpc": "2.0",
	"method": "PrintReceipt",
	"params": {
		"beginFiscalReceiptInput": {
			"operatorName": "???????????????? 1",
			"operatorNumber": 1,
			"usn": ""
		},
		"receiptItems": [
			{
				"description": "?????????????? 1",
				"enumVatCategory": "B",
				"price": 2.4,
				"quantity": 5,
				"surchargeAmount": 0,
				"type": "article"
			},
			{
				"description": "?????????????? 2",
				"enumVatCategory": "B",
				"price": 3.42,
				"quantity": 3,
				"surchargeAmount": 0,
				"type": "article"
			},
			{
				"text": "???????????????? ?????????? ?????????? ??????????????",
				"type": "text"
			}
		],
		"receiptPayments": [
			{
				"amount": 22.26,
				"medium": "MEDIUM_CASH"
			}
		],
		"textAfterPayment": [
			{
				"text": "???????????????? ?????????? ???????? ??????????????",
				"type": "text"
			},
			{
				"text": "123456789",
				"type": "barcode"
			}
		]
	}
}
```


The following Fiscal Command Response Object shows the answer of the upper request:

```json
{
	"result": {
		"qr": "51300001*0000326*2021-04-07*19:09:42*22.26"
	},
	"id": 1,
	"jsonrpc": "2.0"
}



```