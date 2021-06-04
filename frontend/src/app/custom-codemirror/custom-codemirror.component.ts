import { Component, Input, OnInit, AfterViewInit, OnChanges, SimpleChanges } from '@angular/core';
import * as CodeMirror from 'codemirror';
import { CodemirrorService } from '../shared/shared-services/codemirror.service';
import * as yaml from 'yaml';

import 'codemirror/mode/yaml/yaml';
import 'codemirror/lib/codemirror';
import 'codemirror/addon/lint/lint';
import 'codemirror/addon/lint/yaml-lint';
import 'codemirror/addon/fold/foldgutter';
import 'codemirror/addon/fold/indent-fold';
import 'codemirror/addon/fold/foldcode';

import 'codemirror/addon/edit/closebrackets';
import 'codemirror/addon/edit/matchbrackets';
import { CodeEditor } from '../shared/shared-services/codemirror.config';


@Component({
  selector: 'app-custom-codemirror',
  templateUrl: './custom-codemirror.component.html',
  styleUrls: ['./custom-codemirror.component.css']
})
export class CustomCodemirrorComponent implements OnInit, AfterViewInit, OnChanges {

  static get Prefix(): string {
    return "codemirror-";
  }
  @Input() content: any;
  @Input() id!: string;

  private codemirror: any;

  SPACES_TO_ONE_TAB = 2;
  SPACE_REPLACE = '';

  CODEMIRROR_CONFIG: any = {
    theme: 'idea',
    mode: 'yaml',
    lineNumbers: true,
    foldGutter: true,
    tabSize: this.SPACES_TO_ONE_TAB,
    indentUnit: this.SPACES_TO_ONE_TAB,
    indentWithTabs: true,
    gutters: [
      'CodeMirror-linenumbers',
      'CodeMirror-foldgutter',
      'CodeMirror-lint-markers'
    ],
    autoCloseBrackets: true,
    matchBrackets: true,
    autofocus: true
  };

  constructor(private _codemirrorService: CodemirrorService) {
    this.SPACE_REPLACE = ' '.repeat(this.SPACES_TO_ONE_TAB);
    this._codemirrorService.editor = CodeEditor.JSON;
  }

  ngOnInit(): void {
      
  }
  ngAfterViewInit(): void {
    this.codemirror = CodeMirror.fromTextArea(document.getElementById(`${this.prefix}${this.id}`) as HTMLTextAreaElement,
      this.CODEMIRROR_CONFIG
      );
      this.codemirror.setSize('100%', 350);
      
      this.codemirror.refresh();
  }
  ngOnChanges(changes: SimpleChanges): void {

    this.content = this.content || "";
    this.codemirror?.setValue(this.content || "");
    this.codemirror?.refresh();
    if(this.codemirror)
    this.update();

  }

  private update(): void{
    //console.log("This is content")
   // console.log(this.content);
    
    this._codemirrorService.mergeEditorConstruct(
      this.codemirror,
      JSON.parse(JSON.stringify(this.CODEMIRROR_CONFIG)),
      yaml.parse(this.content)
    );

    setTimeout(() => {
      this._codemirrorService.showEditor();
      setTimeout(() => {
        //this._codemirrorService.updateCodeMirrorVisual(this.getProfiles(), response.propertyList, jsonObject);
        //this.SUGGESTED_LIST = this.codemirrorService.findSuggestedPropertyList('');
      }, 200);
    }, 500);
  }
  get prefix(): string {
    return CustomCodemirrorComponent.Prefix;
  }

}
