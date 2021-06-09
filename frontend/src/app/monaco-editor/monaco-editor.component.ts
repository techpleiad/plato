import { Component, Input, OnInit, AfterViewInit, OnChanges, SimpleChanges } from '@angular/core';
import { DiffEditorModel } from 'ngx-monaco-editor';

@Component({
  selector: 'app-monaco-editor',
  templateUrl: './monaco-editor.component.html',
  styleUrls: ['./monaco-editor.component.css']
})
export class MonacoEditorComponent implements OnInit {
  @Input() data1: string="";
  @Input() data2: string="";
  
 // text1 = "";
  //text2 = "";
  //isCompared = false;

  inputOptions = { theme: "vs", language: 'yaml' };

  diffOptions = { theme: "vs", language: "yaml", readOnly: false, renderSideBySide: true, originalEditable: true };
  originalModel: DiffEditorModel = {
    code: '',
    language: 'plaintext'
  };
 
  modifiedModel: DiffEditorModel = {
    code: '',
    language: 'plaintext'
  };

  constructor() { }


  

  ngOnInit(): void {
  }
  ngOnChanges(): void{
    this.originalModel = Object.assign({}, this.originalModel, { code: this.data1 });
    this.modifiedModel = Object.assign({}, this.originalModel, { code: this.data2 });
  }
  
  /*
  onCompare() {
    this.originalModel = Object.assign({}, this.originalModel, { code: this.text1 });
    this.modifiedModel = Object.assign({}, this.originalModel, { code: this.text2 });
    this.isCompared = true;
    window.scrollTo(0, 0); // scroll the window to top
  }*/

}
