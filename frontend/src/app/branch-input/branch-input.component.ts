import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { microService } from '../microService';

@Component({
  selector: 'app-branch-input',
  templateUrl: './branch-input.component.html',
  styleUrls: ['./branch-input.component.css']
})
export class BranchInputComponent implements OnInit {
  @Input() mservice!: microService;
  @Output() branch_sent = new EventEmitter();

  constructor() { }

  ngOnInit(): void {
  }
  sendBranch(event: any){
    //console.log(event);
    
    this.branch_sent.emit(event.value);
    
  }
  

}
