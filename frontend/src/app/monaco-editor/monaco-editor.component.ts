import { Component, OnInit } from '@angular/core';
import { DiffEditorModel } from 'ngx-monaco-editor';

@Component({
  selector: 'app-monaco-editor',
  templateUrl: './monaco-editor.component.html',
  styleUrls: ['./monaco-editor.component.css']
})
export class MonacoEditorComponent implements OnInit {

  constructor() { }

  text1 = "";
  text2 = "";
  isCompared = false;

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

  ngOnInit(): void {
  }
  onCompare() {
    this.originalModel = Object.assign({}, this.originalModel, { code: this.text1 });
    this.modifiedModel = Object.assign({}, this.originalModel, { code: this.text2 });
    this.isCompared = true;
    window.scrollTo(0, 0); // scroll the window to top
  }

}
