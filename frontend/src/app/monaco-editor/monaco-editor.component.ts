import { Component, Input, OnInit, AfterViewInit, OnChanges, SimpleChanges, Output, EventEmitter } from '@angular/core';
import { DiffEditorModel } from 'ngx-monaco-editor';

@Component({
  selector: 'app-monaco-editor',
  templateUrl: './monaco-editor.component.html',
  styleUrls: ['./monaco-editor.component.css']
})
export class MonacoEditorComponent implements OnInit {
  @Input() data1: string="";  //original -> destination
  @Input() data2: string=""; // modified -> source
  @Output() modifySourceData  = new EventEmitter();
  
  text1 = "";
  text2 = "";
  //isCompared = false;

  inputOptions = { theme: "vs", language: 'yaml' };

  diffOptions = { theme: "vs", language: "yaml", readOnly: false, renderSideBySide: false, originalEditable: true };
  originalModel: DiffEditorModel = {
    code: '',
    language: 'yaml'
  };
 
  modifiedModel: DiffEditorModel = {
    code: '',
    language: 'yaml'
  };

  constructor() { }


  

  ngOnInit(): void {
  }
  ngOnChanges(): void{
    this.originalModel = Object.assign({}, this.originalModel, { code: this.data1 });
    console.log(this.originalModel);
    this.modifiedModel = Object.assign({}, this.originalModel, { code: this.data2 });
  }
  onInitDiffEditor(diffEditor: any) {
    if (!diffEditor) {
      return;
    }
  
    diffEditor.getModifiedEditor().onDidChangeModelContent(() => {
      const content = diffEditor.getModel().modified.getValue();
      console.log(content);
      this.modifySourceData.emit(content);
    });
    /* We never change the content of original model as it is the destination  */
    diffEditor.getOriginalEditor().onDidChangeModelContent(() => {
      const content = diffEditor.getModel().original.getValue();
      console.log(content);
    });
    //console.log(diffEditor.getOriginalEditor.getValue())
  }
  
  /*
  onCompare() {
    this.originalModel = Object.assign({}, this.originalModel, { code: this.text1 });
    this.modifiedModel = Object.assign({}, this.originalModel, { code: this.text2 });
    this.isCompared = true;
    window.scrollTo(0, 0); // scroll the window to top
  }*/

}
