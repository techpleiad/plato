import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'headingPipe'
})
export class HeadingPipePipe implements PipeTransform {

  transform(value: string): string{
    let cleanStr = value.replace(/-/g," ");
    var separateWord = cleanStr.toLowerCase().split(' ');
    for (var i = 0; i < separateWord.length; i++) {
      separateWord[i] = separateWord[i].charAt(0).toUpperCase() +
      separateWord[i].substring(1);
    }
    cleanStr = separateWord.join(' ');
    return cleanStr;
  }
    
}


