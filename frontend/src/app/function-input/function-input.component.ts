import { Component, EventEmitter, OnInit, Output } from '@angular/core';

@Component({
  selector: 'app-function-input',
  templateUrl: './function-input.component.html',
  styleUrls: ['./function-input.component.css']
})
export class FunctionInputComponent implements OnInit {
  @Output() function_sent = new EventEmitter();

  constructor() { }
  

  ngOnInit(): void {
  }
  sendFunction(event: any){
    this.function_sent.emit(event.value);
  }

}
