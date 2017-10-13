Translating Unicoded Sentence To English
========================================

### Problem: Translation
Let us see how we can use google APIs, for translation of 'Tamil, Chinese unicoded' keywords to English keywords.

### Solution:
Machine learning based translation is giving better results these days and are more matured now. 
There are popular translation APIs provided starting from Google to Bing... 
Many are free and very cheap to use, Rather than building a new translation system on our own we can use them.

### Code sample:
php code using google api for translating from Tamil (unicode), Chinese (unicode) sentences to English sentences.


		<html>
		<body>
			<?php
		
			//English to Tamil translation
			$English = "Hello sir";
			$ToTranslate = urlencode($English);
		
			$url = 'https://translate.googleapis.com/translate_a/single?client=gtx&sl=en&tl=ta&dt=t&q='.$ToTranslate;
			$GetTranslation = file_get_contents($url);
			echo $GetTranslation;
			echo "<br>";
		

			//Tamil to English translation
			$Tamil ='வணக்கம் ஐயா';
			$ToTranslate = rawurlencode($Tamil);
		
			$url = 'https://translate.googleapis.com/translate_a/single?client=gtx&sl=ta&tl=en&dt=t&ie=UTF-8&oe=UTF-8&q='.$ToTranslate;
			$GetTranslation = file_get_contents($url);
			echo $GetTranslation;


			//Chinese to English translation
			$Chinese ='白癡論 不知者 張盛聞扛上黃偉益';
			$ToTranslate = rawurlencode($Chinese);

			$url = 'https://translate.googleapis.com/translate_a/single?client=gtx&sl=zh-CN&tl=en&dt=t&ie=UTF-8&oe=UTF-8&q='.$ToTranslate;
			$GetTranslation = file_get_contents($url);
			echo $GetTranslation;
		
			?>
		</body>
		</html>


