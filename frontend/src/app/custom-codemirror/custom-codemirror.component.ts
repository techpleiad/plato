import { Component, Input, OnInit, AfterViewInit, OnChanges, SimpleChanges, Output, EventEmitter } from '@angular/core';
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
import { ProfileSpecTO, PropertyDetail } from '../shared/models/ProfileSpecTO';
import { ColorProviderService } from '../shared/shared-services/color-provider.service';
import { ProfileDataTO } from '../shared/models/ProfileDataTO';
import { YamlService } from '../shared/shared-services/yaml.service';



@Component({
  selector: 'app-custom-codemirror',
  templateUrl: './custom-codemirror.component.html',
  styleUrls: ['./custom-codemirror.component.css']
})
export class CustomCodemirrorComponent implements OnInit, AfterViewInit, OnChanges {
  @Output() modifyProfileData = new EventEmitter()

  static get Prefix(): string {
    return "codemirror-";
  }
  @Input() content: string="";
  @Input() id!: string;
  @Input() propertyList: PropertyDetail[] = [];
  @Input() ownerList: string[] = [];
  @Input() cmp: string = "";
  @Input() codemirrorMode = "YAML";
  @Input() codemirrorHeight = "400px";
  @Input() codemirrorWidth = "100%";
  @Input() isEditable = false;
  @Input() isJsonSchemaEditor = false;
  contentValid = true;


  private codemirror: any;

  SPACES_TO_ONE_TAB = 2;
  SPACE_REPLACE = '';
  profileColorList: ProfileDataTO[]=[];

  CODEMIRROR_CONFIG: any = {
    readonly: false,
    theme: 'idea',
    mode: 'yaml',
    lineNumbers: true,
    foldGutter: false,
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

  constructor(private _codemirrorService: CodemirrorService, private _colorService: ColorProviderService, private yamlFileService: YamlService) {
    this.SPACE_REPLACE = ' '.repeat(this.SPACES_TO_ONE_TAB);
    this._codemirrorService.editor = CodeEditor.YAML;
  }

  ngOnInit(): void {
    console.log(this.isEditable);
    this.yamlFileService.errorObservable$.subscribe((data:boolean)=>{
      this.contentValid = !data;
    })
  }
  ngAfterViewInit(): void {
    this.codemirror = CodeMirror.fromTextArea(document.getElementById(`${this.prefix}${this.id}`) as HTMLTextAreaElement,
      this.CODEMIRROR_CONFIG
      );

      this.codemirror.setSize('100%', this.codemirrorHeight);
      this.codemirror.refresh();
      if(this.codemirror){
        this._colorService.reset();
        if(this.content!=="")
        this.update();
      }
      this.codemirror.on('change',(editor: any)=>{
        //console.log(editor.getValue());
        let newContent = this.yamlFileService.replaceAll(editor.getValue(),'\t',this.SPACE_REPLACE);
        if(this.codemirrorMode === "YAML")
          this.yamlFileService.validateYAML(newContent);
        if(this.codemirrorMode === "JSON")
          this.yamlFileService.validateJSON(newContent);
        this.modifyProfileData.emit(newContent);
      })
      this.codemirror.on('dblclick', (instance: any, event: Event) => {
        let schemaProperty = (this._codemirrorService.lineToPropertyBreadcrumbMap.get(instance.getCursor().line+1));
        console.log(schemaProperty);
        if(this.isJsonSchemaEditor){
          let type = this.getSchemaPropertyType(schemaProperty);
          console.log(type);
        }
        
      });
  }
  ngOnChanges(changes: SimpleChanges): void {
    console.log("something changed");
    //console.log(changes);

    if(this.codemirrorMode==="JSON"){
      this._codemirrorService.editor = CodeEditor.JSON;
    }

    //console.log(this.ownerList);
    this.content = this.content || "";
    this.profileColorList = [];
    this.codemirror?.refresh();
    if(this.codemirror){
      this._colorService.reset();
      if(this.content !== "")
        this.update();
      else{
        this._codemirrorService.content = "";
      }
    }
  }

  private update(): void{
    const jsonObject = yaml.parse(this.content);
    //console.log("This is content")
   // console.log(this.content);
   
    
    this._codemirrorService.mergeEditorConstruct(
      this.codemirror,
      JSON.parse(JSON.stringify(this.CODEMIRROR_CONFIG)),
      jsonObject,
      `${this.prefix}${this.id}-container`,
      this.cmp
    );

    this.profileColorList = [];
    this.profileColorList = this.ownerList.map((val:string)=>{
      return new ProfileDataTO(val,this._colorService.getColor());
    })
    //console.log(this.profileColorList);
    //console.log(this.propertyList);

    setTimeout(() => {
      this._codemirrorService.showEditor(this.codemirrorHeight,this.codemirrorWidth);
      setTimeout(() => {
        this._codemirrorService.updateCodeMirrorVisual(this.profileColorList, this.propertyList, jsonObject,`${this.prefix}${this.id}-container`,this.cmp);
        //this.SUGGESTED_LIST = this.codemirrorService.findSuggestedPropertyList('');
      }, 200);
    }, 1000);
    
  }
  get prefix(): string {
    return CustomCodemirrorComponent.Prefix;
  }

  getSchemaPropertyType(schemaProperty: string){
    let jsonSchemaContent = JSON.parse(this.content);
    let parentList = schemaProperty.split(".");
    let curr = jsonSchemaContent;
    for(let i=0;i<parentList.length;i++){
      if(!curr[parentList[i]]){
            curr[parentList[i]] = {};
      }
      curr = curr[parentList[i]];
    }
    console.log(curr);
    return curr.type;
    /*
    curr["minLength"] = 2;
    this.content = JSON.stringify(jsonSchemaContent,null,2);
    this.update();*/
    

  }

}


// constructor() -> ngOnInit() -> ngOnChanges() -> ngAfterViewInit() -> ngOnDestroy()