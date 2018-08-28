select FOLDERID from ARFOLDER where FOLDER='OTP_Auth';

select FOLDERID from ARDEVICE where  BANKID in (select BANKID from ARBANKINFO where BANKDIRNAME = 'testbank_tool_1');

update ARDEVICE set FOLDERID=(select FOLDERID from ARFOLDER where FOLDER='OTP_Auth') where  BANKID in (select BANKID from ARBANKINFO where BANKDIRNAME = 'testbank_tool_1') and RANGEID=0;
commit;


select FOLDERID from ARDEVICE where  BANKID in (select BANKID from ARBANKINFO where BANKDIRNAME = 'testbank_tool_1');

select * from  ARBANKCALLOUTS where BANKID in (select bankid from arbankinfo where BANKDIRNAME='testbank_tool_1');


select FOLDERID from ARFOLDER where FOLDER='OTP_Auth';


select FOLDERID from ARDEVICE where  BANKID in (select BANKID from ARBANKINFO where BANKDIRNAME = 'testbank_tool_2');

update ARDEVICE set FOLDERID=(select FOLDERID from ARFOLDER where FOLDER='OTP_Auth') where  BANKID in (select BANKID from ARBANKINFO where BANKDIRNAME = 'testbank_tool_2') and RANGEID=0;
commit;


select FOLDERID from ARDEVICE where  BANKID in (select BANKID from ARBANKINFO where BANKDIRNAME = 'testbank_tool_2');

select * from  ARBANKCALLOUTS where BANKID in (select bankid from arbankinfo where BANKDIRNAME='testbank_tool_2');