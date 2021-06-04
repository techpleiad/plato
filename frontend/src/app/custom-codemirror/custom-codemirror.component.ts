import { Component, Input, OnInit, AfterViewInit } from '@angular/core';
import * as CodeMirror from 'codemirror';
import { CodemirrorService } from '../shared/shared-services/codemirror.service';

import 'codemirror/mode/yaml/yaml';
import 'codemirror/lib/codemirror';
import 'codemirror/addon/lint/lint';
import 'codemirror/addon/lint/yaml-lint';
import 'codemirror/addon/fold/foldgutter';
import 'codemirror/addon/fold/indent-fold';
import 'codemirror/addon/fold/foldcode';

import 'codemirror/addon/edit/closebrackets';
import 'codemirror/addon/edit/matchbrackets';


@Component({
  selector: 'app-custom-codemirror',
  templateUrl: './custom-codemirror.component.html',
  styleUrls: ['./custom-codemirror.component.css']
})
export class CustomCodemirrorComponent implements OnInit, AfterViewInit {

  static get Prefix(): string {
    return "codemirror-";
  }
  @Input() content: any;
  @Input() id!: string;

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
  }

  ngOnInit(): void {
      
  }
  ngAfterViewInit(): void {
    const codemirror = CodeMirror.fromTextArea(document.getElementById(`${this.prefix}${this.id}`) as HTMLTextAreaElement,
      this.CODEMIRROR_CONFIG
      );
      codemirror.setSize('100%', 350);
      codemirror.refresh();
      //console.log(codemirror);
  }
  get prefix(): string {
    return CustomCodemirrorComponent.Prefix;
  }

}
